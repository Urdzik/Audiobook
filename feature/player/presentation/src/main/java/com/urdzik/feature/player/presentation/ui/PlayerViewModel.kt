package com.urdzik.feature.player.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urdzik.core.common.CoroutineScopeLaunchWithHandlerBehaviour
import com.urdzik.core.common.ViewModelControllerContext
import com.urdzik.core.ui.toFormatTime
import com.urdzik.feature.chapters.presentation.contoller.ChaptersUIController
import com.urdzik.feature.player.presentation.contoller.PlayerUIController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

class PlayerViewModel : ViewModel(), CoroutineScopeLaunchWithHandlerBehaviour,
    KoinScopeComponent {

    override val scope: Scope by playerViewModelScope()

    private val playerUIController: PlayerUIController by scope.inject()
    private val chaptersUIController: ChaptersUIController by scope.inject()

    private val playerScreenType = MutableStateFlow(PlayerScreenType.Player)

    val uiState: StateFlow<PlayerScreenUIState> = combine(
        playerUIController.uiState,
        chaptersUIController.uiState,
        playerScreenType
    ) { player, chapters, playerScreenType ->
        PlayerScreenUIState(
            title = player.title,
            description = player.description,
            imageUrl = player.imageUrl,
            isPlaying = player.isPlaying,
            progress = player.progress,
            totalAudioTime = player.totalAudioTime.toFormatTime(),
            currentAudioTime = player.currentAudioTime.toFormatTime(),
            speed = player.speed,
            chapters = chapters.chapters,
            currentChapter = player.currentChapter,
            playerScreenType = playerScreenType
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlayerScreenUIState()
    )

    private val _stateEvent = MutableStateFlow(PlayerScreenStateEvent())
    val stateEvent: StateFlow<PlayerScreenStateEvent> = _stateEvent.asStateFlow()

    fun obtainEvent(stateEvent: PlayerScreenEvent) {
        when (stateEvent) {
            is PlayerScreenEvent.PlayerEvent -> playerUIController.obtainEvent(stateEvent)
            is PlayerScreenEvent.ChaptersEvent -> chaptersUIController.obtainEvent(stateEvent)
            is PlayerScreenEvent.ChangeScreenType -> {
                playerScreenType.value = stateEvent.type
            }
        }
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
        playerUIController.onCleared()
    }
}

private fun <T> T.playerViewModelScope() where T : ViewModel, T : KoinScopeComponent =
    lazy {
        val scope = createScope()
        addCloseable {
            scope.close()
        }
        scope.declare<ViewModelControllerContext>(object : ViewModelControllerContext {
            override val coroutineScope: CoroutineScope get() = viewModelScope

            override fun addCloseable(closeable: () -> Unit) {
                this@playerViewModelScope.addCloseable {
                    closeable()
                }
            }

            override val scopeID: ScopeID get() = scope.id
        })
        scope
    }
