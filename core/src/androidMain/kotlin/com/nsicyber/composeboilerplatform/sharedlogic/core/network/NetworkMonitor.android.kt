package com.nsicyber.composeboilerplatform.sharedlogic.core.network

private class AndroidNetworkMonitor : NetworkMonitor {
    override fun isOnline(): Boolean = true
}

actual fun networkMonitor(): NetworkMonitor = AndroidNetworkMonitor()
