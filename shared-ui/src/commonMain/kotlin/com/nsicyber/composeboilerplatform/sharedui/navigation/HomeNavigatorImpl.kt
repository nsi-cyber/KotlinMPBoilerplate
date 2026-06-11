package com.nsicyber.composeboilerplatform.sharedui.navigation

import com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base.IBaseNavigator
import com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base.NavigatorController
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation.HomeNavigator

/**
 * Home navigator implementation bound to the root Nav3 stack.
 */
class HomeNavigatorImpl(
    navigatorController: NavigatorController
) : IBaseNavigator(navigatorController), HomeNavigator {
    override fun navigateToHomeDetailsDemo() {
        navigatorController.navigate(AppRoutes.HOME_DETAILS_DEMO)
    }
}
