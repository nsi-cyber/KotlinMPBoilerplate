package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model

import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.UiText

/**
 * Marker interface for global dialog requests rendered by the app host.
 */
sealed interface DialogRequest {
    /**
     * Minimal informational dialog request.
     */
    data class Alert(
        val title: UiText,
        val message: UiText,
        val confirmLabel: UiText = UiText.DynamicString("OK")
    ) : DialogRequest

    /**
     * Confirmation dialog request with positive and negative actions.
     */
    data class Confirm(
        val title: UiText,
        val message: UiText,
        val confirmLabel: UiText = UiText.DynamicString("Confirm"),
        val dismissLabel: UiText = UiText.DynamicString("Cancel")
    ) : DialogRequest
}
