package com.nsicyber.composeboilerplatform.sharedui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation.HomeNavigator
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation.HomeScreen
import com.nsicyber.composeboilerplatform.sharedui.overlay.AppOverlayHosts
import com.nsicyber.composeboilerplatform.sharedui.overlay.OverlayProvider
import com.nsicyber.composeboilerplatform.sharedui.overlay.rememberOverlayManagers

/**
 * Shared Compose application root consumed by Android and iOS hosts.
 */
@Composable
fun AppRoot() {
    val overlayManagers = rememberOverlayManagers()
    val homeNavigator = remember(overlayManagers) {
        object : HomeNavigator {
            override fun navigateBack() = Unit

            override fun navigateToHomeDetailsDemo() {
                overlayManagers.snackbarManager.showSuccess("Navigator demo action handled in AppRoot.")
            }
        }
    }

    MaterialTheme {
        OverlayProvider(managers = overlayManagers) {
            AppOverlayHosts(managers = overlayManagers) {
                HomeScreen(homeNavigator = homeNavigator)
            }
        }
    }
}
