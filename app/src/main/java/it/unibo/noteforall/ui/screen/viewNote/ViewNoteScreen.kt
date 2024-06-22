package it.unibo.noteforall.ui.screen.viewNote

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.ui.composables.NoteCardExtended

@Composable
fun ViewNoteScreen(navController: NavHostController, noteId: String, db: FirebaseFirestore) {
    LazyColumn {
        item {
            NoteCardExtended(navController, noteId, db)
        }
    }
}
