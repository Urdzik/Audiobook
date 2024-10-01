package com.urdzik.feature.player.presentation.ui

import com.urdzik.core.common.StringResource
import com.urdzik.feature.player.data.model.Chapter

data class PlayerScreenUIState(
    val title: StringResource? = null,
    val description: StringResource? = null,
    val imageUrl: String? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val totalAudioTime: String = "00:00",
    val currentAudioTime: String = "00:00",
    val speed: Speed = Speed.X1,
    val chapters: List<Chapter> = emptyList(),
    val currentChapter: Chapter? = null,
    val playerScreenType: PlayerScreenType = PlayerScreenType.Player,
)

data class PlayerUIState(
    val title: StringResource? = null,
    val description: StringResource? = null,
    val imageUrl: String? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val totalAudioTime: Long = 0L,
    val currentAudioTime: Long = 0L,
    val speed: Speed = Speed.X1,
    val currentChapter: Chapter? = null,
)

data class ChaptersUIState(
    val chapters: List<Chapter> = emptyList(),
)

enum class Speed(val value: Float, val text: StringResource) {
    X05(0.5f, StringResource(audiobook.R.string.speed_0_5)),
    X075(0.75f, StringResource(audiobook.R.string.speed_0_75)),
    X1(1f, StringResource(audiobook.R.string.speed_1_0)),
    X15(1.5f, StringResource(audiobook.R.string.speed_1_5)),
    X2(2f, StringResource(audiobook.R.string.speed_2_0)),;

    companion object{
        fun getIndexByValue(value: Float): Int {
            return entries.toTypedArray().indexOfFirst { it.value == value }
        }
    }
}

enum class PlayerScreenType {
    Player,
    Chapters,
}


sealed interface PlayerScreenEvent {
    sealed interface PlayerEvent: PlayerScreenEvent {
        data object PlayOrPause : PlayerEvent
        data object NextArticle : PlayerEvent
        data object PreviousArticle : PlayerEvent
        data object MoveForward : PlayerEvent
        data object MoveBackward : PlayerEvent
        @JvmInline
        value class ChangeSpeed(val speed: Speed) : PlayerEvent
        @JvmInline
        value class SeekTo(val position: Float) : PlayerEvent
    }

    sealed interface ChaptersEvent: PlayerScreenEvent {
        data class ChangeChapter(val index: Int) : ChaptersEvent
    }

    @JvmInline
    value class ChangeScreenType(val type: PlayerScreenType) : PlayerScreenEvent
}