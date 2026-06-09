package com.nsicyber.composeboilerplatform.sharedui

import androidx.compose.ui.window.ComposeUIViewController

/**
 * iOS entry point that wraps the shared root composable in a UIKit controller.
 */
fun MainViewController() = ComposeUIViewController { AppRoot() }
