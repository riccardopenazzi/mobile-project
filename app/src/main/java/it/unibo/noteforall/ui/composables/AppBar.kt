package it.unibo.noteforall.ui.composables

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterAlt
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
import it.unibo.noteforall.utils.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, navController: NavHostController? = null) {
    CenterAlignedTopAppBar(
        title = {
            Text(title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
        },
        navigationIcon = {
            if ((title == "Note") || (title == "Profile") || (title == "Edit Profile")) {
                IconButton(onClick = {
                    navController?.popBackStack()
                    Log.i("debBack", "Premuto go back")
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if ((title == "Home") || (title == "Saved")) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.FilterAlt,
                        tint = Color.White,
                        contentDescription = "Filter button"
                    )
                }
            } else if (title == "My Profile") {
                IconButton(onClick = {
                    navController?.navigate(Screens.EditProfile.screen) {
                        popUpTo(0)
                    }
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