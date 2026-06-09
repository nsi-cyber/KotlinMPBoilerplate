package com.nsicyber.composeboilerplatform.sharedui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.asString
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.BottomSheetRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.DialogRequest
import com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.overlay.model.SnackbarMessageType

/**
 * Hosts content and renders all global overlay layers.
 */
@Composable
fun AppOverlayHosts(
    managers: OverlayManagers,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()

        SnackbarOverlayHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            manager = managers.snackbarManager
        )

        DialogOverlayHost(manager = managers.dialogManager)

        BottomSheetOverlayHost(manager = managers.bottomSheetManager)
    }
}

/**
 * Renders currently queued snackbar items.
 */
@Composable
private fun SnackbarOverlayHost(
    manager: SnackbarManagerImpl,
    modifier: Modifier = Modifier
) {
    val messages by manager.messages.collectAsState()
    if (messages.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.take(3).forEach { item ->
            val containerColor = when (item.type) {
                SnackbarMessageType.INFO -> MaterialTheme.colorScheme.inverseSurface
                SnackbarMessageType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                SnackbarMessageType.ERROR -> MaterialTheme.colorScheme.errorContainer
            }

            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = containerColor,
                action = {
                    Button(onClick = { manager.dismiss(item.id) }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(item.message.asString())
            }
        }
    }
}

/**
 * Renders the currently active global dialog request.
 */
@Composable
private fun DialogOverlayHost(
    manager: DialogManagerImpl
) {
    when (val request = manager.currentDialog.collectAsState().value) {
        is DialogRequest.Alert -> {
            AlertDialog(
                onDismissRequest = manager::dismiss,
                title = { Text(request.title.asString()) },
                text = { Text(request.message.asString()) },
                confirmButton = {
                    Button(onClick = manager::dismiss) {
                        Text(request.confirmLabel.asString())
                    }
                }
            )
        }

        is DialogRequest.Confirm -> {
            AlertDialog(
                onDismissRequest = manager::dismiss,
                title = { Text(request.title.asString()) },
                text = { Text(request.message.asString()) },
                confirmButton = {
                    Button(onClick = manager::dismiss) {
                        Text(request.confirmLabel.asString())
                    }
                },
                dismissButton = {
                    Button(onClick = manager::dismiss) {
                        Text(request.dismissLabel.asString())
                    }
                }
            )
        }

        null -> Unit
    }
}

/**
 * Renders the currently active bottom sheet request.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetOverlayHost(
    manager: BottomSheetManagerImpl
) {
    val request = manager.currentSheet.collectAsState().value ?: return
    when (request) {
        is BottomSheetRequest.Basic -> {
            ModalBottomSheet(
                onDismissRequest = manager::dismiss
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = request.title.asString(), style = MaterialTheme.typography.titleMedium)
                    Text(text = request.message.asString())
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = manager::dismiss
                    ) {
                        Text(request.actionLabel.asString())
                    }
                }
            }
        }
    }
}
