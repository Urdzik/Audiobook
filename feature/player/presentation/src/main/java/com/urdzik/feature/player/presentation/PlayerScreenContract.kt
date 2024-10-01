package com.urdzik.feature.player.presentation

import com.urdzik.core.api.contract.model.BookResponce
import com.urdzik.core.api.contract.model.Chapter
import com.urdzik.core.common.StringResource

data class PlayerScreenUIState(
    val title: StringResource,
    val description: StringResource,
    val imageUrl: String,
    val isPlaying: Boolean,
    val progress: Float = 0f,
    val totalAudioTime: String = "00:00",
    val currentAudioTime: String = "00:00",
    val speed: Speed,
    val book: BookResponce,
    val currentChapter: Chapter,
    val playerScreenType: PlayerScreenType = PlayerScreenType.Player,
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
    data object PlayOrPause : PlayerScreenEvent
    data object NextArticle : PlayerScreenEvent
    data object PreviousArticle : PlayerScreenEvent
    data object MoveForward : PlayerScreenEvent
    data object MoveBackward : PlayerScreenEvent
    @JvmInline
    value class ChangeSpeed(val speed: Speed) : PlayerScreenEvent
    @JvmInline
    value class SeekTo(val position: Float) : PlayerScreenEvent
    @JvmInline
    value class ChangeScreenType(val type: PlayerScreenType) : PlayerScreenEvent
    @JvmInline
    value class ChangeChapter(val chapter: Chapter) : PlayerScreenEvent
}