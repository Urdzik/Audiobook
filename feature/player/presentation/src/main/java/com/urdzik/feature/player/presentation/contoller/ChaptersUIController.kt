package com.urdzik.feature.chapters.presentation.contoller

import com.urdzik.core.common.ControllerCreationType
import com.urdzik.core.common.CoroutineScopeLaunchWithHandlerBehaviour
import com.urdzik.core.common.ViewModelControllerContext
import com.urdzik.core.common.controllerDefinition
import com.urdzik.feature.player.data.repository.PlayerRepository
import com.urdzik.feature.player.domain.manager.AudioManager
import com.urdzik.feature.player.presentation.ui.ChaptersUIState
import com.urdzik.feature.player.presentation.ui.PlayerScreenEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.module.dsl.new
import org.koin.dsl.ScopeDSL

fun ScopeDSL.getChaptersUIControllerDefinition() = controllerDefinition(
    getContext = { new(::ChaptersUIControllerContext) },
    getController = { new(::ChaptersUIController) },
    controllerCreationType = ControllerCreationType.Factory,
)

data class ChaptersUIControllerContext(
    val viewModelControllerContext: ViewModelControllerContext
) : ViewModelControllerContext by viewModelControllerContext

class ChaptersUIController(
    private val viewModelControllerContext: ViewModelControllerContext,
    private val audioController: AudioManager,
    private val playerRepository: PlayerRepository
) : CoroutineScopeLaunchWithHandlerBehaviour {

    private val _uiState = MutableStateFlow(ChaptersUIState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelControllerContext.coroutineScope.launch {
            val book = playerRepository.getBookById()
            _uiState.value = ChaptersUIState(
                chapters = book.chapter,
            )
        }
    }

    fun obtainEvent(event: PlayerScreenEvent.ChaptersEvent) {
        when (event) {
            is PlayerScreenEvent.ChaptersEvent.ChangeChapter -> {
                viewModelControllerContext.coroutineScope.launch {
                    audioController.changeChapter(index = event.index)
                }
            }
        }
    }
}
