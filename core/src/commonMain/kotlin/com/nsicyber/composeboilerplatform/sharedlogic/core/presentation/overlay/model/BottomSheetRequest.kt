package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText

/**
 * Marker interface for bottom sheet requests handled by the app-level host.
 */
sealed interface BottomSheetRequest {
    /**
     * Generic filler bottom sheet request for boilerplate usage.
     */
    data class Basic(
        val title: UiText,
        val message: UiText,
        val actionLabel: UiText = UiText.DynamicString("Close")
    ) : BottomSheetRequest
}
