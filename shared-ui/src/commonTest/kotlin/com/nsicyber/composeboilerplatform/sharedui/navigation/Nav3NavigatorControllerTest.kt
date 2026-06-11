package com.nsicyber.composeboilerplatform.sharedui.navigation

import androidx.compose.runtime.mutableStateListOf
import kotlin.test.Test
import kotlin.test.assertEquals

class Nav3NavigatorControllerTest {

    @Test
    fun navigateAndPopBackStack_updatesCurrentAndPreviousRoutes() {
        val rootBackStack = mutableStateListOf<AppRootDestination>(RootTabsDestination)
        val controller = Nav3NavigatorController(rootBackStack)

        controller.navigate(AppRoutes.HOME_DETAILS_DEMO)

        assertEquals(AppRoutes.HOME_DETAILS_DEMO, controller.currentRoute)
        assertEquals(AppRoutes.ROOT_TABS, controller.previousRoute)

        controller.popBackStack()

        assertEquals(AppRoutes.ROOT_TABS, controller.currentRoute)
        assertEquals(null, controller.previousRoute)
    }

    @Test
    fun popUpTo_respectsInclusiveFlag() {
        val rootBackStack = mutableStateListOf<AppRootDestination>(
            RootTabsDestination,
            HomeDetailsDemoDestination,
            HomeDetailsDemoDestination
        )
        val controller = Nav3NavigatorController(rootBackStack)

        controller.popUpTo(AppRoutes.HOME_DETAILS_DEMO, inclusive = false)
        assertEquals(3, rootBackStack.size)
        assertEquals(AppRoutes.HOME_DETAILS_DEMO, controller.currentRoute)

        controller.popUpTo(AppRoutes.ROOT_TABS, inclusive = false)
        assertEquals(1, rootBackStack.size)
        assertEquals(AppRoutes.ROOT_TABS, controller.currentRoute)

        controller.navigate(AppRoutes.HOME_DETAILS_DEMO)
        controller.popUpTo(AppRoutes.ROOT_TABS, inclusive = true)
        assertEquals(1, rootBackStack.size)
        assertEquals(AppRoutes.ROOT_TABS, controller.currentRoute)
    }
}
