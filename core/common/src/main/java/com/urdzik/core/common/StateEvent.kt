package com.urdzik.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope


/**
 *  This [StateEvent] can only have two primitive states.
 *  https://github.com/leonard-palm/compose-state-events
 */
@Immutable
sealed interface StateEvent {
    /**
     *  The event is currently in its triggered state
     */
    @Immutable
    object Triggered : StateEvent {
        override fun toString(): String = "triggered"
    }

    /**
     *  The event is currently in its consumed state
     */
    @Immutable
    object Consumed : StateEvent {
        override fun toString(): String = "consumed"
    }
}

/**
 *  Shorter and more readable version of [StateEvent.Triggered]
 */
val triggered = StateEvent.Triggered

/**
 *  Shorter and more readable version of [StateEvent.Consumed]
 */
val consumed = StateEvent.Consumed

/**
 *  This [StateEventWithContent] can have exactly 2 states like the [StateEvent] but the triggered state holds a value of type [T].
 */
@Immutable
sealed interface StateEventWithContent<T>

/**
 * The event in its triggered state holding a value of [T]. See [triggered] to create an instance of this.
 * @param content A value that is needed on the event consumer side.
 */
@Immutable
class StateEventWithContentTriggered<T>(val content: T) : StateEventWithContent<T> {
    override fun toString(): String = "triggered($content)"
}

/**
 * The event in its consumed state not holding any value. See [consumed] to create an instance of this.
 */
@Immutable
class StateEventWithContentConsumed<T> : StateEventWithContent<T> {
    override fun toString(): String = "consumed"
}

/**
 * A shorter and more readable way to create an [StateEventWithContent] in its triggered state holding a value of [T].
 * @param content A value that is needed on the event consumer side.
 */
fun <T> triggered(content: T) = StateEventWithContentTriggered(content)

/**
 * A shorter and more readable way to create an [StateEventWithContent] in its consumed state.
 */
fun <T> consumed() = StateEventWithContentConsumed<T>()

/**
 *  A side effect that gets executed when the given [event] changes to its triggered state.
 *
 *  @param event Pass the state event to be listened to from your view-state.
 *  @param onConsumed In this callback you are advised to set the passed [event] to [StateEvent.Consumed] in your view-state.
 *  @param action Callback that gets called in the composition's [CoroutineContext]. Perform the actual action this [event] leads to.
 */
@Composable
@NonRestartableComposable
fun EventEffect(event: StateEvent, onConsumed: () -> Unit, action: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(key1 = event, key2 = onConsumed) {
        if (event is StateEvent.Triggered) {
            action()
            onConsumed()
        }
    }
}

@Composable
@NonRestartableComposable
fun EventEffect(
    event: StateEvent,
    onConsumed: () -> Unit,
    isAtLeast: Lifecycle.Event,
    action: suspend () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val isAtLeastCondition by produceState(isAtLeast == Lifecycle.Event.ON_CREATE, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            value = event.ordinal >= isAtLeast.ordinal
        }
        lifecycle.addObserver(observer = observer)
        awaitDispose {
            lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(
        event,
        onConsumed,
        isAtLeastCondition
    ) {
        if (event is StateEvent.Triggered && isAtLeastCondition) {
            action()
            onConsumed()
        }
    }
}

/**
 *  A side effect that gets executed when the given [event] changes to its triggered state.
 *
 *  @param event Pass the state event of type [T] to be listened to from your view-state.
 *  @param onConsumed In this callback you are advised to set the passed [event] to an instance of [StateEventWithContentConsumed] in your view-state (see [consumed]).
 *  @param action Callback that gets called in the composition's [CoroutineContext]. Perform the actual action this [event] leads to. The actual content of the [event] will be passed as an argument.
 */
@Composable
@NonRestartableComposable
fun <T> EventEffect(
    event: StateEventWithContent<T>,
    onConsumed: (T) -> Unit = {},
    action: suspend (T) -> Unit
) {
    LaunchedEffect(key1 = event, key2 = onConsumed) {
        if (event is StateEventWithContentTriggered<T>) {
            action(event.content)
            onConsumed(event.content)
        }
    }
}