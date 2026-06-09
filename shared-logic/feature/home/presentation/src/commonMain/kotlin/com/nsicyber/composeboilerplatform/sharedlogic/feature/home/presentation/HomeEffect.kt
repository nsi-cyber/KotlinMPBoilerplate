package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

/**
 * Home feature one-shot side effects.
 *
 * This is intentionally small for boilerplate and can be expanded per feature needs.
 */
sealed interface HomeEffect {
    data object None : HomeEffect
}
