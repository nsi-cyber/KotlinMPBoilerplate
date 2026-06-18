package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for controlling the currently visible global bottom sheet.
 */
interface BottomSheetManager {
    val currentSheet: StateFlow<BottomSheetRequest?>

    fun show(sheetRequest: BottomSheetRequest)

    fun dismiss()
}
