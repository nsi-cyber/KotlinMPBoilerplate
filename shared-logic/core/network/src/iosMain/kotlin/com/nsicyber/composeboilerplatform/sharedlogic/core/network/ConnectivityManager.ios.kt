@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

private class IOSConnectivityManagerImpl : ConnectivityManager {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val isConnected: StateFlow<Boolean> = callbackFlow {
        val monitor = nw_path_monitor_create()

        nw_path_monitor_set_update_handler(monitor) { path ->
            trySend(nw_path_get_status(path) == nw_path_status_satisfied)
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)

        awaitClose {
            nw_path_monitor_cancel(monitor)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .stateIn(
            scope = managerScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = true
        )
}

private object IOSConnectivityHolder {
    val manager: ConnectivityManager by lazy { IOSConnectivityManagerImpl() }
}

actual fun connectivityManager(): ConnectivityManager = IOSConnectivityHolder.manager

actual fun initializePlatformConnectivity(context: Any?) = Unit
