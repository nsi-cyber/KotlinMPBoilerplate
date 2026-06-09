package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for controlling the currently visible global dialog.
 */
interface DialogManager {
    val currentDialog: StateFlow<DialogRequest?>

    fun show(dialogRequest: DialogRequest)

    fun dismiss()
}
