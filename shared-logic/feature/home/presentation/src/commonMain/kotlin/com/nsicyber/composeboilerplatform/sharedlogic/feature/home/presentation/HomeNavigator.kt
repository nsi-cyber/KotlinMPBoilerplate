package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

import com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base.IViewNavigator

/**
 * Feature-level navigator contract for Home flow.
 */
interface HomeNavigator : IViewNavigator {
    fun navigateToHomeDetailsDemo()
}
