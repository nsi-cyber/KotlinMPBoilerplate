package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay

import androidx.compose.runtime.compositionLocalOf
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private object NoOpSnackbarManager : SnackbarManager {
    private val internalState = MutableStateFlow(emptyList<SnackbarMessage>())
    override val messages: StateFlow<List<SnackbarMessage>> = internalState.asStateFlow()
    override fun show(message: SnackbarMessage) = Unit
    override fun dismiss(messageId: Long) = Unit
}

private object NoOpDialogManager : DialogManager {
    private val internalState = MutableStateFlow<DialogRequest?>(null)
    override val currentDialog: StateFlow<DialogRequest?> = internalState.asStateFlow()
    override fun show(dialogRequest: DialogRequest) = Unit
    override fun dismiss() = Unit
}

private object NoOpBottomSheetManager : BottomSheetManager {
    private val internalState = MutableStateFlow<BottomSheetRequest?>(null)
    override val currentSheet: StateFlow<BottomSheetRequest?> = internalState.asStateFlow()
    override fun show(sheetRequest: BottomSheetRequest) = Unit
    override fun dismiss() = Unit
}

/**
 * CompositionLocal for globally accessible snackbar manager contract.
 */
val LocalSnackbarManager = compositionLocalOf<SnackbarManager> { NoOpSnackbarManager }

/**
 * CompositionLocal for globally accessible dialog manager contract.
 */
val LocalDialogManager = compositionLocalOf<DialogManager> { NoOpDialogManager }

/**
 * CompositionLocal for globally accessible bottom sheet manager contract.
 */
val LocalBottomSheetManager = compositionLocalOf<BottomSheetManager> { NoOpBottomSheetManager }
