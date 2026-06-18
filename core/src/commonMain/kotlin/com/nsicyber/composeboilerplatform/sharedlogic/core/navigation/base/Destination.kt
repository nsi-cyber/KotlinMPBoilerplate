package com.nsicyber.composeboilerplatform.sharedlogic.core.navigation.base

/**
 * Reusable route descriptor used by navigator implementations.
 *
 * `host` defines the static route root, while `params` define path placeholders.
 * `setPath` can be used to create a concrete route for navigation calls.
 */
open class Destination(
    val host: String,
    vararg val params: String
) {
    private var customPath: String? = null

    /**
     * Route pattern or concrete route depending on whether `setPath` was called.
     */
    val route: String
        get() = customPath ?: buildString {
            append(host)
            params.forEach { param ->
                append("/{$param}")
            }
        }

    /**
     * Creates a concrete route path by replacing placeholder arguments with values.
     */
    fun setPath(vararg path: Any): Destination {
        customPath = buildString {
            append(host)
            path.forEach { part ->
                append("/$part")
            }
        }
        return this
    }
}
