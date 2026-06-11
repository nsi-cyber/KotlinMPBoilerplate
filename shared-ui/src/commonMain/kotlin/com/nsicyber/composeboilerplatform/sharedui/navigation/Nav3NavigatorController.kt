package com.nsicyber.composeboilerplatform.sharedui.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base.NavigatorController

/**
 * Nav3-backed implementation of the shared framework-agnostic navigator controller.
 */
class Nav3NavigatorController(
    private val rootBackStack: SnapshotStateList<AppRootDestination>
) : NavigatorController {

    override val currentRoute: String?
        get() = rootBackStack.lastOrNull()?.route()

    override val previousRoute: String?
        get() = rootBackStack.getOrNull(rootBackStack.lastIndex - 1)?.route()

    override fun navigate(route: String) {
        val destination = routeToRootDestination(route) ?: return
        rootBackStack.add(destination)
    }

    override fun popBackStack() {
        if (rootBackStack.size > 1) {
            rootBackStack.removeAt(rootBackStack.lastIndex)
        }
    }

    override fun popUpTo(route: String, inclusive: Boolean) {
        val targetIndex = rootBackStack.indexOfLast { destination -> destination.route() == route }
        if (targetIndex == -1) return

        val removeFrom = if (inclusive) targetIndex else targetIndex + 1
        if (removeFrom >= rootBackStack.size) return

        repeat(rootBackStack.size - removeFrom) {
            rootBackStack.removeAt(rootBackStack.lastIndex)
        }

        if (rootBackStack.isEmpty()) {
            rootBackStack.add(RootTabsDestination)
        }
    }
}
