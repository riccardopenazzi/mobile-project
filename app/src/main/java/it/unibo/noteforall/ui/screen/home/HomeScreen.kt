package it.unibo.noteforall.ui.screen.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(),
                onClick = { navController.navigate(NoteForAllRoute.NewNote.route) }
            ) {
                Icon(Icons.Outlined.Add, "New post")
                Text(text = "New post")
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.padding(contentPadding).fillMaxSize(),
        ) {
            for (i in 0..6 ) {
                item { NoteCard(navController = navController) }
            }
        }
    }
}
