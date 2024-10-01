package com.urdzik.core.common

import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.ScopeID

interface ViewModelControllerContext {

    val coroutineScope: CoroutineScope

    fun addCloseable(closeable: () -> Unit) {}

    val scopeID: ScopeID
}
