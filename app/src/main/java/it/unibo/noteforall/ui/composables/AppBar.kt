package it.unibo.noteforall.ui.composables

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import it.unibo.noteforall.data.NoteForAllDatabase
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavHostController? = null, currentRoute: NoteForAllRoute, internalDb: NoteForAllDatabase) {
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
                (currentRoute.title == "Note") ||
                (currentRoute.title == "Profile") ||
                (currentRoute.title == "Edit Profile") ||
                (currentRoute.title == "New Note")
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back button"
                    )
                }
            }
            if (currentRoute.title == "My Profile") {
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
            if ((currentRoute.title == "Home") || (currentRoute.title == "Saved")) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.FilterAlt,
                        tint = Color.White,
                        contentDescription = "Filter button"
                    )
                }
            } else if (currentRoute.title == "My Profile") {
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