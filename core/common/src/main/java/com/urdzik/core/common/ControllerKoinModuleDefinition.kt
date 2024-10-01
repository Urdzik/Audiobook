package com.urdzik.core.common

import org.koin.core.definition.KoinDefinition
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.onOptions
import org.koin.core.module.dsl.withOptions
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

inline fun <reified Controller : Any> ScopeDSL.controllerDefinition(
    crossinline getController: Scope.(params: ParametersHolder) -> Controller,
    secondaryTypes: List<KClass<*>> = emptyList(),
    qualifier: Qualifier? = null,
    controllerCreationType: ControllerCreationType = ControllerCreationType.Scoped,
    creationDefinitions: ScopeDSL.() -> Unit = {},
    createdAtStart: Boolean = false
): KoinDefinition<Controller> {
    creationDefinitions()

    return when (controllerCreationType) {
        ControllerCreationType.Factory -> factory(qualifier = qualifier) { params ->
            getController(params)
        }

        ControllerCreationType.Scoped -> scoped(qualifier = qualifier) { params ->
            getController(params)
        }
    } withOptions {
        this.secondaryTypes = secondaryTypes
        if (createdAtStart) {
            createdAtStart()
        }
    }
}

inline fun <reified Context : Any, reified Controller : Any> ScopeDSL.controllerDefinition(
    noinline getContext: (Scope.(params: ParametersHolder) -> Context),
    crossinline getController: Scope.(params: ParametersHolder) -> Controller,
    secondaryTypes: List<KClass<*>> = emptyList(),
    qualifier: Qualifier? = null,
    controllerCreationType: ControllerCreationType = ControllerCreationType.Scoped,
    creationDefinitions: ScopeDSL.() -> Unit = {},
    createdAtStart: Boolean = false
): KoinDefinition<Controller> {
    creationDefinitions()

    return when (controllerCreationType) {
        ControllerCreationType.Factory -> factory(qualifier = qualifier) { params ->
            val context = getContext(params)
            declare(context)
            getController(params)
        }

        ControllerCreationType.Scoped -> scoped(qualifier = qualifier) {params ->
            val context = getContext(params)
            declare(context)
            getController(params)
        }
    } withOptions {
        this.secondaryTypes = secondaryTypes
        if (createdAtStart) {
            createdAtStart()
        }
    }
}

sealed interface ControllerCreationType {
    object Scoped : ControllerCreationType
    object Factory : ControllerCreationType
}
