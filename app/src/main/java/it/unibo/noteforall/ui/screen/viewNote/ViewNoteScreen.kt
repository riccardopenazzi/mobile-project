package it.unibo.noteforall.ui.screen.viewNote

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.ui.composables.NoteCardExtended
import it.unibo.noteforall.utils.Note

@Composable
fun ViewNoteScreen(navController: NavHostController, noteId: String, db: FirebaseFirestore) {
    LazyColumn {
        item {
            NoteCardExtended(navController, noteId, db)
        }
    }
}
