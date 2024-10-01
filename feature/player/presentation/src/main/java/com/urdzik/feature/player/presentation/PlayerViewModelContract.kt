package com.urdzik.feature.player.presentation

import com.urdzik.core.common.StateEvent
import com.urdzik.core.common.consumed

data class PlayerScreenStateEvent(
    val something: StateEvent = consumed
)

sealed interface PlayerScreenStateEventConsumed {
   data object DoSomething : PlayerScreenStateEventConsumed
}