package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
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

private object AndroidConnectivityContextHolder {
    var appContext: Context? = null
    var manager: ConnectivityManager? = null
}

/**
 * Initializes Android connectivity dependencies once from the application host.
 */
actual fun initializePlatformConnectivity(context: Any?) {
    val androidContext = context as? Context ?: return
    AndroidConnectivityContextHolder.appContext = androidContext.applicationContext
    if (AndroidConnectivityContextHolder.manager == null) {
        AndroidConnectivityContextHolder.manager = AndroidConnectivityManagerImpl(androidContext.applicationContext)
    }
}

private class AndroidConnectivityManagerImpl(
    context: Context
) : ConnectivityManager {
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

    private fun currentConnectionState(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }

    override val isConnected: StateFlow<Boolean> = callbackFlow {
        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
            }
        }

        trySend(currentConnectionState())
        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .stateIn(
            scope = managerScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = currentConnectionState()
        )
}

private class FallbackConnectivityManager : ConnectivityManager {
    override val isConnected: StateFlow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(true)
}

actual fun connectivityManager(): ConnectivityManager {
    AndroidConnectivityContextHolder.manager?.let { return it }

    val context = AndroidConnectivityContextHolder.appContext ?: return FallbackConnectivityManager()
    return AndroidConnectivityManagerImpl(context).also {
        AndroidConnectivityContextHolder.manager = it
    }
}
