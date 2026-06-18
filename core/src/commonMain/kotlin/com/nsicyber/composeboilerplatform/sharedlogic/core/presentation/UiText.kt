package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * UI-safe text model that can represent either dynamic strings or resource-backed strings.
 */
sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    data class StringResourceId(
        val id: StringResource,
        val args: Array<String> = emptyArray()
    ) : UiText
}

/**
 * Resolves UiText to String in composable context.
 */
@Composable
fun UiText.asString(): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResourceId -> stringResource(resource = id, formatArgs = args)
    }
}

/**
 * Resolves UiText to String in suspend context.
 */
suspend fun UiText.toStringSuspend(): String {
    return when (this) {
        is UiText.DynamicString -> value
        is UiText.StringResourceId -> getString(resource = id, formatArgs = args)
    }
}
