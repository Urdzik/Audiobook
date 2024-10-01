package com.urdzik.feature.player.domain.manager

import com.urdzik.feature.player.data.model.Chapter
import com.urdzik.feature.player.domain.PlayerState

data class AudioState(
    val playerState: PlayerState,
    val currentMusic: Chapter?,
    val currentPosition: Long,
    val totalDuration: Long,
    val isShuffleEnabled: Boolean,
    val isRepeatOneEnabled: Boolean
)