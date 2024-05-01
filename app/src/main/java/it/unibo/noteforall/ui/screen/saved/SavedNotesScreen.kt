package it.unibo.noteforall.ui.screen.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun SavedNotesScreen(navController: NavHostController, db: FirebaseFirestore) {
    var isLaunched by remember { mutableStateOf(false) }
    var isDownloadFinished = remember { AtomicBoolean(false) }
    var posts by remember { mutableStateOf(mutableListOf<Note>()) }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            StorageUtil.loadSavedPosts(posts, isDownloadFinished, db)
            isLaunched = true
        }
    }

    Scaffold () { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (isDownloadFinished.get()) {
                items(posts) { post ->
                    NoteCard(navController = navController, note = post, db = db)
                }
            }
        }
    }
}

