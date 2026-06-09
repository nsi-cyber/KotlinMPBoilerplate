package com.nsicyber.composeboilerplatform.sharedui.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.BottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory bottom sheet manager implementation with one active sheet request.
 */
class BottomSheetManagerImpl : BottomSheetManager {
    private val internalState = MutableStateFlow<BottomSheetRequest?>(null)

    override val currentSheet: StateFlow<BottomSheetRequest?> = internalState.asStateFlow()

    override fun show(sheetRequest: BottomSheetRequest) {
        internalState.value = sheetRequest
    }

    override fun dismiss() {
        internalState.value = null
    }
}
