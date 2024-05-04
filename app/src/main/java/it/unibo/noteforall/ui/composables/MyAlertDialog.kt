package it.unibo.noteforall.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.DialogProperties

@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    title: String,
    text: String,
    icon: ImageVector?
) {
    AlertDialog(
        icon = {
            if (icon != null) Icon(icon, contentDescription = "Alert dialog icon")
        },
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("Confirm")
            }                
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}
