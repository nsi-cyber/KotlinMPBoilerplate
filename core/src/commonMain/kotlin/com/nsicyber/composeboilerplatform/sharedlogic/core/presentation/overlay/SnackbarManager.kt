package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for publishing and dismissing app-level snackbar messages.
 */
interface SnackbarManager {
    val messages: StateFlow<List<SnackbarMessage>>

    fun show(message: SnackbarMessage)

    fun dismiss(messageId: Long)
}
