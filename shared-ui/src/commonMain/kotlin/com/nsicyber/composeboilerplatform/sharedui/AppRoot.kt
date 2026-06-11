package com.nsicyber.composeboilerplatform.sharedui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.nsicyber.composeboilerplatform.sharedui.navigation.AppNavigationHost
import com.nsicyber.composeboilerplatform.sharedui.overlay.AppOverlayHosts
import com.nsicyber.composeboilerplatform.sharedui.overlay.OverlayProvider
import com.nsicyber.composeboilerplatform.sharedui.overlay.rememberOverlayManagers

/**
 * Shared Compose application root consumed by Android and iOS hosts.
 */
@Composable
fun AppRoot() {
    val overlayManagers = rememberOverlayManagers()

    MaterialTheme {
        OverlayProvider(managers = overlayManagers) {
            AppOverlayHosts(managers = overlayManagers) {
                AppNavigationHost()
            }
        }
    }
}
