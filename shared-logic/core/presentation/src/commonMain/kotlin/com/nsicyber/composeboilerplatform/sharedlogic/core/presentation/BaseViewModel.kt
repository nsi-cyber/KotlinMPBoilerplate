package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.mvi.BaseMviViewModel
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.SnackbarManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessageType

/**
 * Feature base ViewModel that combines MVI state handling and global overlay helpers.
 */
abstract class BaseViewModel<STATE, EFFECT, EVENT, NAVIGATOR>(
    initialState: STATE,
    private val snackbarManager: SnackbarManager,
    private val dialogManager: DialogManager,
    private val bottomSheetManager: BottomSheetManager
) : BaseMviViewModel<STATE, EFFECT, EVENT, NAVIGATOR>(initialState) {
    private var nextSnackbarId: Long = 1L

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
