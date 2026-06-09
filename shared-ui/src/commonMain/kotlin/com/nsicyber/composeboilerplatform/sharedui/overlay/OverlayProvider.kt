package com.nsicyber.composeboilerplatform.sharedui.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalBottomSheetManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalDialogManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.LocalSnackbarManager

/**
 * Bundles remembered overlay manager implementations.
 */
data class OverlayManagers(
    val snackbarManager: SnackbarManagerImpl,
    val dialogManager: DialogManagerImpl,
    val bottomSheetManager: BottomSheetManagerImpl
)

/**
 * Creates and remembers all global overlay managers once per composition root.
 */
@Composable
fun rememberOverlayManagers(): OverlayManagers {
    return remember {
        OverlayManagers(
            snackbarManager = SnackbarManagerImpl(),
            dialogManager = DialogManagerImpl(),
            bottomSheetManager = BottomSheetManagerImpl()
        )
    }
}

/**
 * Provides overlay manager contracts to feature presentation code.
 */
@Composable
fun OverlayProvider(
    managers: OverlayManagers,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSnackbarManager provides managers.snackbarManager,
        LocalDialogManager provides managers.dialogManager,
        LocalBottomSheetManager provides managers.bottomSheetManager,
        content = content
    )
}
