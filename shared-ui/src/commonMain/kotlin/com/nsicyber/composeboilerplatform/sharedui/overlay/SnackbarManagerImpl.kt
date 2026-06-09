package com.nsicyber.composeboilerplatform.sharedui.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory snackbar manager implementation backed by a StateFlow list.
 */
class SnackbarManagerImpl : SnackbarManager {
    private val internalState = MutableStateFlow(emptyList<SnackbarMessage>())
    private var nextMessageId: Long = 1L

    override val messages: StateFlow<List<SnackbarMessage>> = internalState.asStateFlow()

    override fun show(message: SnackbarMessage) {
        internalState.value = listOf(message) + internalState.value
    }

    override fun dismiss(messageId: Long) {
        internalState.value = internalState.value.filterNot { it.id == messageId }
    }

    /**
     * Adds a convenience filler success snackbar.
     */
    fun showSuccess(message: String) {
        show(
            SnackbarMessage(
                id = nextMessageId++,
                message = UiText.DynamicString(message),
                type = SnackbarMessageType.SUCCESS
            )
        )
    }

    /**
     * Adds a convenience filler error snackbar.
     */
    fun showError(message: String) {
        show(
            SnackbarMessage(
                id = nextMessageId++,
                message = UiText.DynamicString(message),
                type = SnackbarMessageType.ERROR
            )
        )
    }
}
