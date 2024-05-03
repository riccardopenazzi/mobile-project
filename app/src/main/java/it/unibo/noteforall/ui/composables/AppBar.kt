package it.unibo.noteforall.ui.composables

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavHostController? = null, currentRoute: NoteForAllRoute) {
    var showFiltersDialog by remember { mutableStateOf(false) }
    var categories = remember { mutableStateListOf<String>() }

    if (showFiltersDialog) {
        FiltersDialog(
            categories = listOf("prova", "prova2"),
            onDismiss = { showFiltersDialog = false },
            onConfirm = { showFiltersDialog = false }
        )
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                currentRoute.title,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            if (
                (currentRoute.title == NoteForAllRoute.ViewNote.title) ||
                (currentRoute.title == NoteForAllRoute.Profile.title) ||
                (currentRoute.title == NoteForAllRoute.EditProfile.title) ||
                (currentRoute.title == NoteForAllRoute.NewNote.title) ||
                (currentRoute.title == NoteForAllRoute.Settings.title)
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back button"
                    )
                }
            }
            if (currentRoute.title == NoteForAllRoute.MyProfile.title) {
                IconButton(onClick = { navController?.navigate(NoteForAllRoute.Settings.route) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        tint = Color.White,
                        contentDescription = "Settings"
                    )
                }
            }
        },
        actions = {
            if (
                (currentRoute.title == NoteForAllRoute.Home.title) ||
                (currentRoute.title == NoteForAllRoute.Saved.title) ||
                (currentRoute.title == NoteForAllRoute.Search.title)
            ) {
                IconButton(onClick = { showFiltersDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.FilterAlt,
                        tint = Color.White,
                        contentDescription = "Filter button"
                    )
                }
            } else if (currentRoute.title == NoteForAllRoute.MyProfile.title) {
                IconButton(onClick = {
                    navController?.navigate(NoteForAllRoute.EditProfile.route)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        tint = Color.White,
                        contentDescription = "Edit profile button"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}
