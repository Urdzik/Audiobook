package com.urdzik.feature.player.presentation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urdzik.core.api.contract.FirebaseApi
import com.urdzik.core.api.contract.model.BookResponce
import com.urdzik.core.api.contract.model.Chapter
import com.urdzik.core.common.CoroutineScopeLaunchWithHandlerBehaviour
import com.urdzik.core.common.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayerViewModel() : ViewModel(), CoroutineScopeLaunchWithHandlerBehaviour {

    val uiState: StateFlow<PlayerScreenUIState> = _uiState.asStateFlow()

    private val _stateEvent = MutableStateFlow(PlayerScreenStateEvent())
    val stateEvent: StateFlow<PlayerScreenStateEvent> = _stateEvent.asStateFlow()

    private var currentPlayIndex = 0
    private val currentTime = 0

    init {
        viewModelScope.launch {
            val book = firebaseApi.getBook()
            _uiState.update {
                it.copy(
                    title = StringResource(audiobook.R.string.key_point, 1, book.chapter.size),
                    description = StringResource(book.chapter[currentPlayIndex].title),
                    imageUrl = book.cover,
                    book = book,
                    currentChapter = book.chapter[currentPlayIndex],
                    totalAudioTime = formatTime(book.chapter[currentPlayIndex].duration),
                    progress = currentTime.toFloat() / book.chapter[currentPlayIndex].duration
                )
            }
            Log.d("PlayerViewModel", "_uiState loaded: ${_uiState.value}")
            prepareMediaPlayer(book.chapter[currentPlayIndex].audioUrl)
        }
    }

    val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setOnPreparedListener {
            // Update total time and start playing
            _uiState.update {
                it.copy(
                    progress = 0f
                )
            }
        }
        setOnCompletionListener {
            _uiState.update { it.copy(isPlaying = false) }
        }
    }

    private fun prepareMediaPlayer(audioUrl: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepare()
    }

    private fun updateProgress() {
        val currentPosition = mediaPlayer.currentPosition
        val totalDuration = uiState.value.currentChapter.duration
        _uiState.update {
            it.copy(
                currentAudioTime = formatTime(currentPosition),
                progress = (currentPosition / totalDuration.toFloat())
            )
        }
    }

    fun obtainEvent(stateEvent: PlayerScreenEvent) {
        when (stateEvent) {
            is PlayerScreenEvent.ChangeSpeed -> changeSpeed()
            is PlayerScreenEvent.MoveBackward -> moveBackward()
            is PlayerScreenEvent.MoveForward -> moveForward()
            is PlayerScreenEvent.NextArticle -> nextArticle()
            is PlayerScreenEvent.PlayOrPause -> playOrPause()
            is PlayerScreenEvent.PreviousArticle -> previousArticle()
            is PlayerScreenEvent.SeekTo -> seekTo(stateEvent.position)
            is PlayerScreenEvent.ChangeScreenType -> {
                _uiState.update { it.copy(playerScreenType = stateEvent.type) }
            }

            is PlayerScreenEvent.ChangeChapter -> {
                _uiState.update {
                    it.copy(
                        title = StringResource(stateEvent.chapter.title),
                        description = StringResource(stateEvent.chapter.title),
                        progress = 0f,
                        currentChapter = stateEvent.chapter,
                        totalAudioTime = formatTime(stateEvent.chapter.duration)
                    )
                }
                prepareMediaPlayer(stateEvent.chapter.audioUrl)
                mediaPlayer.start()
                updateProgress()
            }
        }
    }

    private fun changeSpeed() {
        val currentSpeed = uiState.value.speed
        val nextSpeed = Speed.getIndexByValue(currentSpeed.value) + 1
        val newSpeed = if (nextSpeed >= Speed.entries.size) {
            Speed.entries[0]
        } else {
            Speed.entries[nextSpeed]
        }
        mediaPlayer.playbackParams = mediaPlayer.playbackParams.setSpeed(newSpeed.value)
        _uiState.update { it.copy(speed = newSpeed) }
    }

    private fun moveBackward() {
        val newPosition = (mediaPlayer.currentPosition - 15000).coerceAtLeast(0)
        mediaPlayer.seekTo(newPosition)
        updateProgress()
    }

    private fun moveForward() {
        val newPosition = (mediaPlayer.currentPosition + 15000).coerceAtMost(mediaPlayer.duration)
        mediaPlayer.seekTo(newPosition)
        updateProgress()
    }

    private fun nextArticle() {
        val book = uiState.value.book ?: return
        if (currentPlayIndex < book.chapter.size - 1) {
            currentPlayIndex++
            val nextChapter = book.chapter[currentPlayIndex]
            _uiState.update {
                it.copy(
                    title = StringResource(nextChapter.title),
                    description = StringResource(nextChapter.title),
                    progress = 0f,
                    currentChapter = nextChapter,
                    totalAudioTime = formatTime(nextChapter.duration)
                )
            }
            prepareMediaPlayer(nextChapter.audioUrl)
            mediaPlayer.start()
            updateProgress()
        }
    }

    private fun previousArticle() {
        val book = uiState.value.book ?: return
        if (currentPlayIndex > 0) {
            currentPlayIndex--
            val previousChapter = book.chapter[currentPlayIndex]
            _uiState.update {
                it.copy(
                    title = StringResource(previousChapter.title),
                    description = StringResource(previousChapter.title),
                    progress = 0f,
                    currentChapter = previousChapter,
                    totalAudioTime = formatTime(previousChapter.duration)
                )
            }
            prepareMediaPlayer(previousChapter.audioUrl)
            mediaPlayer.start()
            updateProgress()
        }
    }

    private fun playOrPause() {
        if (uiState.value.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }

        // Start updating progress regularly
        viewModelScope.launch {
            while (uiState.value.isPlaying) {
                updateProgress()
                delay(20L) // Update progress every second
            }
        }
    }

    private fun seekTo(position: Float) {
        val newPosition = (position * uiState.value.currentChapter.duration).toInt()
        mediaPlayer.seekTo(newPosition)
        updateProgress()
    }

    fun obtainEventConsumed(stateEvent: PlayerScreenStateEventConsumed) {
        when (stateEvent) {
            is PlayerScreenStateEventConsumed.DoSomething -> {
                // Implement any custom action here
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release() // Release media player resources
    }

    // Utility function to format milliseconds into "MM:SS"
    private fun formatTime(ms: Int): String {
        val minutes = (ms / 1000) / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}