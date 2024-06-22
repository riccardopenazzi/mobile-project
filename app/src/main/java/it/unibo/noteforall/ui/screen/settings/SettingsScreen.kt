package it.unibo.noteforall.ui.screen.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.unibo.noteforall.AuthenticationActivity
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.database.User
import it.unibo.noteforall.ui.composables.MyAlertDialog
import it.unibo.noteforall.ui.composables.outlinedTextFieldColors
import it.unibo.noteforall.utils.CurrentUserSingleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Theme { Light, Dark, System }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    internalDb: NoteForAllDatabase,
    state: ThemeState,
    viewModel: ThemeViewModel
) {

    var expanded by remember { mutableStateOf(false) }
    
    var showDialog by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    if (showDialog) {
        MyAlertDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                startLogout(ctx, internalDb)
                viewModel.deleteTheme()
            },
            title = "Are you sure to logout?",
            text = "",
            icon = null
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(2.dp)
            .fillMaxSize()
    ) {
        item {
            Spacer(Modifier.height(30.dp))
            Box {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = state.theme.toString(),
                        onValueChange = {},
                        label = { Text("Choose the theme: ") },
                        readOnly = true,
                        trailingIcon = { TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        colors = outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(5))
                    ) {
                        Theme.entries.forEach { theme ->
                            DropdownMenuItem(
                                text = { Text(text = theme.toString(), color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    expanded = false
                                    viewModel.changeTheme(theme)
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(40.dp))
            Button(
                onClick = { showDialog = true }
            ) {
                Text(text = "Logout", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Exec logout"
                )
            }
        }
    }
}

fun startLogout(ctx: Context, internalDb: NoteForAllDatabase) {
    val user = User(userId = CurrentUserSingleton.currentUser!!.id, 0.0, 0.0)
    CoroutineScope(Dispatchers.IO).launch {
        internalDb.dao.deleteUserId(user)
    }

    val intent = Intent(ctx, AuthenticationActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    ctx.startActivity(intent)
}
