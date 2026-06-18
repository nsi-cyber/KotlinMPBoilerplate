package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessageType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Feature base ViewModel that combines MVI state handling and global overlay helpers.
 */
abstract class BaseViewModel<STATE, EFFECT, EVENT, NAVIGATOR>(
    initialState: STATE,
    private val snackbarManager: SnackbarManager,
    private val dialogManager: DialogManager,
    private val bottomSheetManager: BottomSheetManager
) {
    private val mutableState = MutableStateFlow(initialState)
    private val mutableEffect = MutableSharedFlow<EFFECT>()
    private var navigatorHolder: NAVIGATOR? = null
    private var nextSnackbarId: Long = 1L

    /**
     * Observable immutable state stream for UI rendering.
     */
    val state: StateFlow<STATE> = mutableState.asStateFlow()

    /**
     * One-shot side effects stream.
     */
    val effect: SharedFlow<EFFECT> = mutableEffect.asSharedFlow()

    /**
     * Optional navigator bound by UI host layer.
     */
    protected val navigator: NAVIGATOR?
        get() = navigatorHolder

    /**
     * Handles UI events.
     */
    abstract fun process(event: EVENT)

    /**
     * Binds feature navigator instance to this ViewModel.
     */
    fun setNavigator(navigator: NAVIGATOR) {
        navigatorHolder = navigator
    }

    /**
     * Updates current state atomically.
     */
    protected fun updateState(transform: (STATE) -> STATE) {
        mutableState.update(transform)
    }

    /**
     * Emits one-time effects.
     */
    protected suspend fun emitEffect(effect: EFFECT) {
        mutableEffect.emit(effect)
    }

    /**
     * Shows an informational snackbar.
     */
    protected fun showInfoSnack(message: UiText) {
        showSnackbar(message = message, type = SnackbarMessageType.INFO)
    }

    /**
     * Shows a success snackbar.
     */
    protected fun showSuccessSnack(message: UiText) {
        showSnackbar(message = message, type = SnackbarMessageType.SUCCESS)
    }

    /**
     * Shows an error snackbar.
     */
    protected fun showErrorSnack(message: UiText) {
        showSnackbar(message = message, type = SnackbarMessageType.ERROR)
    }

    /**
     * Shows a simple alert dialog.
     */
    protected fun showAlert(
        title: UiText,
        message: UiText,
        confirmLabel: UiText = UiText.DynamicString("OK")
    ) {
        dialogManager.show(
            DialogRequest.Alert(
                title = title,
                message = message,
                confirmLabel = confirmLabel
            )
        )
    }

    /**
     * Shows a confirm/cancel dialog.
     */
    protected fun showDialog(
        title: UiText,
        message: UiText,
        confirmLabel: UiText = UiText.DynamicString("Confirm"),
        dismissLabel: UiText = UiText.DynamicString("Cancel")
    ) {
        dialogManager.show(
            DialogRequest.Confirm(
                title = title,
                message = message,
                confirmLabel = confirmLabel,
                dismissLabel = dismissLabel
            )
        )
    }

    /**
     * Shows a generic bottom sheet request.
     */
    protected fun showBottomSheet(
        title: UiText,
        message: UiText,
        actionLabel: UiText = UiText.DynamicString("Close")
    ) {
        bottomSheetManager.show(
            BottomSheetRequest.Basic(
                title = title,
                message = message,
                actionLabel = actionLabel
            )
        )
    }

    private fun showSnackbar(message: UiText, type: SnackbarMessageType) {
        snackbarManager.show(
            SnackbarMessage(
                id = nextSnackbarId++,
                message = message,
                type = type
            )
        )
    }
}
