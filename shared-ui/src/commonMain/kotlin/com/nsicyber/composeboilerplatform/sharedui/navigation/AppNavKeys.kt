package com.nsicyber.composeboilerplatform.sharedui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * String route ids used by framework-agnostic navigator contracts.
 */
object AppRoutes {
    const val ROOT_TABS = "root_tabs"
    const val HOME_DETAILS_DEMO = "home_details_demo"
}

/**
 * Root stack destinations.
 */
sealed interface AppRootDestination : NavKey

data object RootTabsDestination : AppRootDestination
data object HomeDetailsDemoDestination : AppRootDestination

/**
 * Home tab backstack destinations.
 */
sealed interface HomeTabDestination : NavKey

data object HomeTabRootDestination : HomeTabDestination

/**
 * Library tab backstack destinations.
 */
sealed interface LibraryTabDestination : NavKey

data object LibraryTabRootDestination : LibraryTabDestination

enum class AppTab {
    HOME,
    LIBRARY
}

internal fun AppRootDestination.route(): String = when (this) {
    RootTabsDestination -> AppRoutes.ROOT_TABS
    HomeDetailsDemoDestination -> AppRoutes.HOME_DETAILS_DEMO
}

internal fun routeToRootDestination(route: String): AppRootDestination? = when (route) {
    AppRoutes.ROOT_TABS -> RootTabsDestination
    AppRoutes.HOME_DETAILS_DEMO -> HomeDetailsDemoDestination
    else -> null
}
