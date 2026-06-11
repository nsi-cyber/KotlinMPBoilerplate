package com.nsicyber.composeboilerplatform.sharedui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation.HomeScreen

@Composable
fun AppNavigationHost() {
    val navigationState = rememberAppNavigationState()
    val rootController = remember(navigationState.rootBackStack) {
        Nav3NavigatorController(navigationState.rootBackStack)
    }
    val homeNavigator = remember(rootController) {
        HomeNavigatorImpl(rootController)
    }

    NavDisplay(
        backStack = navigationState.rootBackStack,
        onBack = rootController::popBackStack,
        entryProvider = entryProvider {
            entry<RootTabsDestination> {
                RootTabsContent(
                    state = navigationState,
                    homeScreen = {
                        HomeScreen(homeNavigator = homeNavigator)
                    }
                )
            }
            entry<HomeDetailsDemoDestination> {
                HomeDetailsDemoScreen(onBack = rootController::popBackStack)
            }
        }
    )
}

@Composable
private fun RootTabsContent(
    state: AppNavigationState,
    homeScreen: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.selectedTab == AppTab.HOME,
                    onClick = { state.selectedTab = AppTab.HOME },
                    label = { Text("Home") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = state.selectedTab == AppTab.LIBRARY,
                    onClick = { state.selectedTab = AppTab.LIBRARY },
                    label = { Text("Library") },
                    icon = {}
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.selectedTab) {
                AppTab.HOME -> {
                    NavDisplay(
                        backStack = state.homeTabBackStack,
                        onBack = {
                            if (state.homeTabBackStack.size > 1) {
                                state.homeTabBackStack.removeAt(state.homeTabBackStack.lastIndex)
                            }
                        },
                        entryProvider = entryProvider {
                            entry<HomeTabRootDestination> {
                                homeScreen()
                            }
                        }
                    )
                }

                AppTab.LIBRARY -> {
                    NavDisplay(
                        backStack = state.libraryTabBackStack,
                        onBack = {
                            if (state.libraryTabBackStack.size > 1) {
                                state.libraryTabBackStack.removeAt(state.libraryTabBackStack.lastIndex)
                            }
                        },
                        entryProvider = entryProvider {
                            entry<LibraryTabRootDestination> {
                                LibraryPlaceholderScreen()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryPlaceholderScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Library tab placeholder")
    }
}

@Composable
private fun HomeDetailsDemoScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home details demo destination")
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onBack
        ) {
            Text("Go back")
        }
    }
}
