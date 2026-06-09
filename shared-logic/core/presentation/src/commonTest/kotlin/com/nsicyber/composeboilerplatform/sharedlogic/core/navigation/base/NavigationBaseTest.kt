package com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base

import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationBaseTest {

    @Test
    fun destination_buildsPatternAndConcreteRoute() {
        val destination = Destination("home", "id")

        assertEquals("home/{id}", destination.route)

        destination.setPath(42)
        assertEquals("home/42", destination.route)
    }

    @Test
    fun baseNavigator_navigateBackAndHelpers_useControllerContract() {
        val controller = FakeNavigatorController(
            current = "home",
            previous = "root"
        )
        val navigator = object : IBaseNavigator(controller) {}

        navigator.navigateBack()
        navigator.navigateAndPop("details")
        navigator.navigateWithoutAddingToBackStack(Destination("profile").setPath("me"))

        assertEquals(1, controller.popBackStackCount)
        assertEquals(listOf("details", "profile/me"), controller.navigatedRoutes)
        assertEquals(listOf("details:false", "details:false"), controller.popUpToCalls)
    }
}

private class FakeNavigatorController(
    current: String?,
    previous: String?
) : NavigatorController {
    private var _current = current
    override val currentRoute: String?
        get() = _current
    private var _previous = previous
    override val previousRoute: String?
        get() = _previous

    var popBackStackCount: Int = 0
        private set
    val navigatedRoutes: MutableList<String> = mutableListOf()
    val popUpToCalls: MutableList<String> = mutableListOf()

    override fun navigate(route: String) {
        _previous = _current
        _current = route
        navigatedRoutes += route
    }

    override fun popBackStack() {
        popBackStackCount += 1
        _current = _previous
    }

    override fun popUpTo(route: String, inclusive: Boolean) {
        popUpToCalls += "$route:$inclusive"
    }
}
