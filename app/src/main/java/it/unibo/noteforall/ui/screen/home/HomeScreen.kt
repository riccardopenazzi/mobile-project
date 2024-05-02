package it.unibo.noteforall.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadHomePosts
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun HomeScreen(navController: NavHostController, db: FirebaseFirestore) {
    var isLaunched by remember { mutableStateOf(false) }
    var isDownloadFinished = remember { AtomicBoolean(false) }
    var posts by remember { mutableStateOf(mutableListOf<Note>()) }


    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            loadHomePosts(posts, isDownloadFinished, db)
            isLaunched = true
            delay(3000)
        }
    }

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
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            Log.i("debHome", "Valuto condizione per if")
            if (isDownloadFinished.get()) {
                items(posts) { post ->
                    NoteCard(navController = navController, note = post, db = db)
                }
            }
        }
    }
}
