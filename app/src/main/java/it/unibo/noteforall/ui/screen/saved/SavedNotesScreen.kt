package it.unibo.noteforall.ui.screen.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.NoteCard

@Composable
fun SavedNotesScreen(navController: NavHostController) {
    Scaffold () { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PrintUserNotes(navController)
                }
            }
        }
    }
}

@Composable
fun PrintUserNotes(navController: NavHostController) {
    for (i in 0..14) {
        NoteCard(navController = navController)
    }
}
