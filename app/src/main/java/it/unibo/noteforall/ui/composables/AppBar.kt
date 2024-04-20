package it.unibo.noteforall.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import it.unibo.noteforall.ui.theme.Teal800

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
        },
        navigationIcon = {
            if ((title == "Note") || (title == "Profile") || (title == "Edit Profile")) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if ((title == "Home") || (title == "Saved")) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.FilterAlt, "Filter button")
                }
            } else if (title == "My Profile") {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Edit, "Edit profile button")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}