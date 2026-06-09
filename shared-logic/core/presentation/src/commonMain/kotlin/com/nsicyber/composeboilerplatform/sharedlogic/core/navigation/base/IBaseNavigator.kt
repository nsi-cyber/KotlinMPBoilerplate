package com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base

/**
 * Shared base implementation for feature navigators.
 */
abstract class IBaseNavigator(
    protected val navigatorController: NavigatorController
) : IViewNavigator {

    override fun navigateBack() {
        val currentRoute = navigatorController.currentRoute
        val previousRoute = navigatorController.previousRoute
        if (currentRoute != null && previousRoute != null) {
            navigatorController.popBackStack()
        }
    }

    /**
     * Navigates to route or reuses existing back stack entry by popping up to it.
     */
    fun navigateAndPop(route: String) {
        navigatorController.popUpTo(route, inclusive = false)
        navigatorController.navigate(route)
    }

    /**
     * Navigates to a route while preventing duplicate stack growth.
     */
    fun navigateWithoutAddingToBackStack(destination: Destination) {
        val currentRoute = navigatorController.currentRoute
        if (currentRoute != null) {
            navigatorController.popUpTo(currentRoute, inclusive = false)
        }
        navigatorController.navigate(destination.route)
    }
}
