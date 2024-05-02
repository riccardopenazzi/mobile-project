package it.unibo.noteforall.ui.screen.settings

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.unibo.noteforall.AuthenticationActivity
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.database.User
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
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

    val ctx = LocalContext.current


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
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Theme.entries.forEach { theme ->
                            DropdownMenuItem(
                                text = { Text(text = theme.toString()) },
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
                onClick = {
                    val user = User(userId = CurrentUserSingleton.currentUser!!.id)
                    CoroutineScope(Dispatchers.IO).launch {
                        internalDb.dao.deleteUserId(user)
                    }

                    val intent = Intent(ctx, AuthenticationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    ctx.startActivity(intent)
                }
            ) {
                Text("Logout")
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    tint = Color.White,
                    contentDescription = "Exec logout"
                )
            }
        }
    }
}
