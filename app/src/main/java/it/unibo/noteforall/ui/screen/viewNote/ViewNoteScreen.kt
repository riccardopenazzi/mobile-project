package it.unibo.noteforall.ui.screen.viewNote

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.unibo.noteforall.ui.composables.NoteCard

@Composable
fun ViewNoteScreen() {
    LazyColumn {
        item { NoteCard(isExtended = true) }
    }
}