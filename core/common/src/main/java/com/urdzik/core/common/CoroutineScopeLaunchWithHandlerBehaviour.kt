package com.urdzik.core.common

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.async as kotlinxCoroutinesAsync
import kotlinx.coroutines.launch as kotlinxCoroutinesLaunch

interface CoroutineScopeLaunchWithHandlerBehaviour {

    val tag: String
        get() = this::class.java.simpleName

    fun CoroutineScope.launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = kotlinxCoroutinesLaunch(
        context = coroutineContextWithHandler(tag) + context,
        start = start,
        block = block
    )

    fun <T> CoroutineScope.async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
    ) = kotlinxCoroutinesAsync(
        context = coroutineContextWithHandler(tag) + context,
        start = start,
        block = block
    )

}

fun coroutineContextWithHandler(tag: String) =
    Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
        Log.e("$tag", "CoroutineScopeLaunchWithHandlerBehaviour debug: $throwable")
//        Firebase.crashlytics.recordException(throwable)
    }