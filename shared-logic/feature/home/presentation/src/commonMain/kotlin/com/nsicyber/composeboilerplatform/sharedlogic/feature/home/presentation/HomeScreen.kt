package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.connectivityManager

/**
 * Shared Home feature screen rendered from Compose Multiplatform targets.
 */
@Composable
fun HomeScreen(
    homeNavigator: HomeNavigator? = null,
    viewModel: HomeViewModel = rememberHomeViewModel(homeNavigator = homeNavigator)
) {
    val uiState by viewModel.uiState.collectAsState()
    val connectivityManager = remember { connectivityManager() }
    val isConnected by connectivityManager.isConnected.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = uiState.greetingText)
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = if (isConnected) "Connectivity: Online" else "Connectivity: Offline",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = viewModel::onLoadGreetingClick
        ) {
            Text("Load greeting")
        }

        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = "Overlay boilerplate demo",
            style = MaterialTheme.typography.titleSmall
        )

        Button(
            modifier = Modifier.padding(top = 12.dp),
            onClick = viewModel::onShowSnackbarClick
        ) {
            Text("Show Snackbar")
        }

        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = viewModel::onShowDialogClick
        ) {
            Text("Show Dialog")
        }

        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = viewModel::onShowBottomSheetClick
        ) {
            Text("Show BottomSheet")
        }

        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = viewModel::onNavigateDemoClick
        ) {
            Text("Navigate Demo")
        }
    }
}
