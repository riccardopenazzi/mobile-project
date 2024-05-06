package it.unibo.noteforall.ui.screen.saved

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun SavedNotesScreen(navController: NavHostController, db: FirebaseFirestore) {
    var isLaunched by remember { mutableStateOf(false) }
    val posts = remember { mutableStateListOf<Note>() }
    val isEmpty by remember { mutableStateOf(AtomicBoolean(false)) }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            StorageUtil.loadSavedPosts(posts, db, isEmpty)
            isLaunched = true
        }
    }

    Scaffold() { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (isEmpty.get()) {
                item { Text(text = "No post saved yet", textAlign = TextAlign.Center) }
            } else {
                items(posts) { post ->
                    NoteCard(navController = navController, note = post, db = db)
                }
            }
        }
    }
}
