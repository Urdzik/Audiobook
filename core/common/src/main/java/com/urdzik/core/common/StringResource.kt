package com.urdzik.core.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
sealed interface StringResource

@Immutable
private sealed interface StringResourceImpl : StringResource {

    sealed interface Value : StringResourceImpl

    @Immutable
    data class Listified(val values: List<Value>, val separator: String) : StringResourceImpl
}

@Immutable
@JvmInline
private value class StringResourceId(@StringRes val value: Int) : StringResourceImpl.Value

@Immutable
private data class StringResourceIdWithArgs(
    @StringRes val value: Int,
    val args: List<out Any>
) : StringResourceImpl.Value

@Immutable
@JvmInline
private value class StringValue(val value: String) : StringResourceImpl.Value


fun StringResource(@StringRes value: Int): StringResource = StringResourceId(value)
fun StringResource(@StringRes value: Int, vararg args: Any): StringResource = StringResourceIdWithArgs(value, args.toList())

fun StringResource(value: String): StringResource = StringValue(value)


operator fun StringResource.plus(other: StringResource?): StringResource =
    this.plus(other, separator = "")

fun StringResource.plus(other: StringResource?, separator: String = ""): StringResource {

    val otherAsList = when (other) {
        is StringResourceImpl -> when (other) {
            is StringResourceImpl.Listified -> other.values
            is StringResourceImpl.Value -> listOf(other)
        }

        null -> listOf()
    }

    return when (this) {
        is StringResourceImpl -> when (this) {
            is StringResourceImpl.Listified -> StringResourceImpl.Listified(
                values = values + otherAsList,
                separator = separator
            )

            is StringResourceImpl.Value -> StringResourceImpl.Listified(
                values = listOf(element = this) + otherAsList,
                separator = separator
            )
        }
    }
}

@Composable
private fun stringResource(resource: StringResource) = when (resource) {
    is StringResourceImpl -> stringResource(resource)
}

@Composable
private fun stringResource(resource: StringResourceImpl) = when (resource) {
    is StringResourceImpl.Listified -> stringResource(resource)
    is StringResourceImpl.Value -> stringResource(resource)
}

@Composable
private fun stringResource(resource: StringResourceImpl.Value) = when (resource) {
    is StringResourceId -> androidx.compose.ui.res.stringResource(id = resource.value)
    is StringResourceIdWithArgs -> androidx.compose.ui.res.stringResource(
        id = resource.value,
        formatArgs = resource.args.toTypedArray()
    )

    is StringValue -> resource.value
}

@Composable
private fun stringResource(resource: StringResourceImpl.Listified) = resource.values
    .map { resourcePart ->
        stringResource(
            resourcePart
        )
    }.joinToString(separator = "")

val StringResource.composed
    @Composable
    get() = stringResource(this)
