package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Observes live connectivity changes in a platform-agnostic way.
 */
interface ConnectivityManager {
    /**
     * Shared latest connectivity state.
     *
     * This state flow is backed by a cached singleton implementation per platform.
     */
    val isConnected: StateFlow<Boolean>
}

/**
 * Returns the platform-specific connectivity manager implementation.
 */
expect fun connectivityManager(): ConnectivityManager

/**
 * Initializes platform connectivity resources when the platform requires host setup.
 *
 * Android hosts should pass `applicationContext`, while iOS can ignore this call.
 */
expect fun initializePlatformConnectivity(context: Any? = null)
