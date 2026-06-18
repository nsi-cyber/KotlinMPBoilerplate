package com.nsicyber.composeboilerplatform.sharedlogic.core.network

private class IOSNetworkMonitor : NetworkMonitor {
    override fun isOnline(): Boolean = true
}

actual fun networkMonitor(): NetworkMonitor = IOSNetworkMonitor()
