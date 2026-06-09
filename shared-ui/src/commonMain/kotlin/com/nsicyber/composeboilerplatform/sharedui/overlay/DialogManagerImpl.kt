package com.nsicyber.composeboilerplatform.sharedui.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.DialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory dialog manager implementation that keeps a single active request.
 */
class DialogManagerImpl : DialogManager {
    private val internalState = MutableStateFlow<DialogRequest?>(null)

    override val currentDialog: StateFlow<DialogRequest?> = internalState.asStateFlow()

    override fun show(dialogRequest: DialogRequest) {
        internalState.value = dialogRequest
    }

    override fun dismiss() {
        internalState.value = null
    }
}
