package com.urdzik.feature.player.domain.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.urdzik.feature.player.data.service.MediaService
import com.urdzik.feature.player.data.model.Chapter
import com.urdzik.feature.player.domain.PlayerState
import com.urdzik.feature.player.domain.toChapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioManagerImpl(private val context: Context) : AudioManager {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val progress = MutableStateFlow(0L)
    private val state =
        MutableStateFlow(AudioState(PlayerState.STOPPED, null, 0L, 0L, false, false))
    override val mediaState: StateFlow<AudioState> = combine(
        state,
        progress
    ) { eventState, progress ->
        eventState.copy(currentPosition = progress)
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(), state.value)

    private var progressJob: Job? = null

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
    }

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                with(player) {
                    state.update {
                        it.copy(
                            playerState = playbackState.toPlayerState(isPlaying),
                            currentMusic = currentMediaItem?.toChapter(),
                            currentPosition = currentPosition,
                            totalDuration = duration.takeIf { it > 0L } ?: 0L
                        )
                    }
                }
                if (player.isPlaying) startProgressTracking() else stopProgressTracking()
            }
        })
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = coroutineScope.launch {
            while (isActive) {
                mediaController?.let {
                    progress.value = it.currentPosition
                }
                delay(20L)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE, Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    @OptIn(UnstableApi::class)
    override fun addMediaItems(audios: List<Chapter>, imageUrl: String) {
        val mediaItems = audios.map {
            MediaItem.Builder()
                .setMediaId(it.audioUrl)
                .setUri(it.audioUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setDurationMs(it.duration.toLong())
                        .setArtworkUri(Uri.parse(imageUrl))
                        .build()
                )
                .build()
        }
        mediaController?.setMediaItems(mediaItems)
    }

    override fun play(mediaItemIndex: Int) {
        mediaController?.apply {
            seekToDefaultPosition(mediaItemIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun getCurrentMediaItemIndex(): Int = mediaController?.currentMediaItemIndex ?: 0

    override fun getMediaItemCount(): Int = mediaController?.mediaItemCount ?: 0

    override fun getCurrentAudio(): Chapter? = mediaController?.currentMediaItem?.toChapter()

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        stopProgressTracking()
        coroutineScope.cancel()
        context.stopService(Intent(context, MediaService::class.java))
    }

    override fun skipToNextAudio() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousAudio() {
        mediaController?.seekToPrevious()
    }

    @OptIn(UnstableApi::class)
    override fun changeSpeed(speed: Float) {
        mediaController?.setPlaybackSpeed(speed)
    }

    @OptIn(UnstableApi::class)
    override fun changeChapter(index: Int) {
        val mediaItemCount = mediaController?.mediaItemCount ?: 0
        if (index in 0 until mediaItemCount) {
            mediaController?.apply {
                seekToDefaultPosition(index)
                playWhenReady = true
                prepare()
            }
        }
    }
}