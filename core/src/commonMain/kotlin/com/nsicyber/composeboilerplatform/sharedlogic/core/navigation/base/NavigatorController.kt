package com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base

/**
 * Framework-agnostic navigation controller used by base navigators.
 *
 * UI modules can implement this contract with any navigation library.
 */
interface NavigatorController {
    val currentRoute: String?
    val previousRoute: String?

    fun navigate(route: String)

    fun popBackStack()

    fun popUpTo(route: String, inclusive: Boolean = false)
}
