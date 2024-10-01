package com.urdzik.feature.player.presentation.contoller

import audiobook.R
import com.urdzik.core.common.ControllerCreationType
import com.urdzik.core.common.CoroutineScopeLaunchWithHandlerBehaviour
import com.urdzik.core.common.StringResource
import com.urdzik.core.common.ViewModelControllerContext
import com.urdzik.core.common.controllerDefinition
import com.urdzik.feature.player.data.model.Chapter
import com.urdzik.feature.player.data.repository.PlayerRepository
import com.urdzik.feature.player.domain.manager.AudioManager
import com.urdzik.feature.player.presentation.ui.PlayerScreenEvent
import com.urdzik.feature.player.presentation.ui.PlayerUIState
import com.urdzik.feature.player.presentation.ui.Speed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.new
import org.koin.dsl.ScopeDSL

fun ScopeDSL.getPlayerUIControllerDefinition() = controllerDefinition(
    getContext = { new(::PlayerUIControllerContext) },
    getController = { new(::PlayerUIController) },
    controllerCreationType = ControllerCreationType.Factory,
)

data class PlayerUIControllerContext(
    val viewModelControllerContext: ViewModelControllerContext
) : ViewModelControllerContext by viewModelControllerContext

class PlayerUIController(
    private val viewModelControllerContext: ViewModelControllerContext,
    private val audioController: AudioManager,
    private val playerRepository: PlayerRepository
)  {

    private val _uiState = MutableStateFlow(PlayerUIState())
    val uiState = _uiState.asStateFlow()

    private var bookId: String? = null

    init {
        viewModelControllerContext.coroutineScope.launch {
            audioController.mediaState.collect { playerState ->
                _uiState.update {
                    it.copy(
                        isPlaying = playerState.playerState == com.urdzik.feature.player.domain.PlayerState.PLAYING,
                        currentChapter = playerState.currentMusic?.copy(duration = playerState.currentPosition.toInt())
                            ?: Chapter(duration = playerState.currentPosition.toInt()),
                        totalAudioTime = playerState.totalDuration,
                        currentAudioTime = playerState.currentPosition,
                        progress = if (playerState.totalDuration > 0) {
                            playerState.currentPosition.toFloat() / playerState.totalDuration.toFloat()
                        } else {
                            0f
                        },
                        description = playerState.currentMusic?.title?.let { it1 ->
                            StringResource(
                                it1
                            )
                        },
                        title = StringResource(
                            R.string.key_point,
                            audioController.getCurrentMediaItemIndex() + 1,
                            audioController.getMediaItemCount()
                        )
                    )
                }
            }
        }

        viewModelControllerContext.coroutineScope.launch {
            playerRepository.getBookById().also {
                bookId = it.id
                val index =
                    playerRepository.getLastChapterIdByBookId(it.id)?.let { audioId ->
                        it.chapter.indexOfFirst { it.audioUrl == audioId }
                    } ?: 0
                _uiState.value = _uiState.value.copy(
                    imageUrl = it.cover,
                    currentChapter = it.chapter.firstOrNull {
                        it.audioUrl == playerRepository.getLastChapterIdByBookId()
                    }
                )
                audioController.addMediaItems(it.chapter, imageUrl = it.cover)
                audioController.changeChapter(index = index)
            }
        }
    }

    fun obtainEvent(event: PlayerScreenEvent.PlayerEvent) {
        when (event) {
            is PlayerScreenEvent.PlayerEvent.PlayOrPause -> playOrPause()
            is PlayerScreenEvent.PlayerEvent.NextArticle -> nextArticle()
            is PlayerScreenEvent.PlayerEvent.PreviousArticle -> previousArticle()
            is PlayerScreenEvent.PlayerEvent.ChangeSpeed -> changeSpeed()
            is PlayerScreenEvent.PlayerEvent.MoveBackward -> moveBackward()
            is PlayerScreenEvent.PlayerEvent.MoveForward -> moveForward()
            is PlayerScreenEvent.PlayerEvent.SeekTo -> snipTo(event.position)
        }
    }

    fun onCleared() {
        audioController.destroy()
    }

    private fun saveProgress() {
        viewModelControllerContext.coroutineScope.launch {
            playerRepository.satLastChapterIdByBookId(
                chapterId = uiState.value.currentChapter?.mediaId ?: ""
            )
        }
    }

    private fun playOrPause() {
        saveProgress()
        if (_uiState.value.isPlaying) {
            audioController.pause()
        } else {
            audioController.resume()
        }
    }

    private fun nextArticle() {
        audioController.skipToNextAudio()
        saveProgress()
    }

    private fun previousArticle() {
        audioController.skipToPreviousAudio()
        saveProgress()
    }

    private fun changeSpeed() {
        val currentSpeed = uiState.value.speed
        val nextSpeed = Speed.getIndexByValue(currentSpeed.value) + 1
        val newSpeed = if (nextSpeed >= Speed.entries.size) {
            Speed.entries[0]
        } else {
            Speed.entries[nextSpeed]
        }
        audioController.changeSpeed(newSpeed.value)
        _uiState.update { it.copy(speed = newSpeed) }
    }

    private fun moveBackward() {
        if (_uiState.value.currentAudioTime < 5000) {
            audioController.seekTo(0)
            return
        }
        val newPosition = _uiState.value.currentAudioTime - 5000
        audioController.seekTo(newPosition)
    }

    private fun moveForward() {
        val newPosition = _uiState.value.currentAudioTime + 10000
        audioController.seekTo(newPosition)
    }

    private fun snipTo(position: Float) {
        val newPosition = (position * uiState.value.totalAudioTime).toLong()
        audioController.seekTo(newPosition)
    }

}
