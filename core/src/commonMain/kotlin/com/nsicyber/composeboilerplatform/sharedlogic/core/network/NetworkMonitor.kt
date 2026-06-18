package com.nsicyber.composeboilerplatform.sharedlogic.core.network

/**
 * Abstraction for network availability checks.
 */
interface NetworkMonitor {
    fun isOnline(): Boolean
}

/**
 * Returns a platform-specific network monitor implementation.
 */
expect fun networkMonitor(): NetworkMonitor
