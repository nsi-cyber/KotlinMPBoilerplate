package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText

/**
 * Visual category for a snackbar message.
 */
enum class SnackbarMessageType {
    INFO,
    SUCCESS,
    ERROR
}

/**
 * Immutable snackbar payload consumed by app-level overlay hosts.
 */
data class SnackbarMessage(
    val id: Long,
    val message: UiText,
    val type: SnackbarMessageType = SnackbarMessageType.INFO
)
