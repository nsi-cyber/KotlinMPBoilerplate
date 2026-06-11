package com.nsicyber.composeboilerplatform.sharedui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * Holds all app-level back stacks used by Nav3 displays.
 */
class AppNavigationState(
    val rootBackStack: SnapshotStateList<AppRootDestination>,
    val homeTabBackStack: SnapshotStateList<HomeTabDestination>,
    val libraryTabBackStack: SnapshotStateList<LibraryTabDestination>,
    initialSelectedTab: AppTab
) {
    var selectedTab: AppTab by mutableStateOf(initialSelectedTab)
}

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    val rootBackStack = remember {
        mutableStateListOf<AppRootDestination>(RootTabsDestination)
    }
    val homeTabBackStack = remember {
        mutableStateListOf<HomeTabDestination>(HomeTabRootDestination)
    }
    val libraryTabBackStack = remember {
        mutableStateListOf<LibraryTabDestination>(LibraryTabRootDestination)
    }

    return remember(rootBackStack, homeTabBackStack, libraryTabBackStack) {
        AppNavigationState(
            rootBackStack = rootBackStack,
            homeTabBackStack = homeTabBackStack,
            libraryTabBackStack = libraryTabBackStack,
            initialSelectedTab = AppTab.HOME
        )
    }
}
