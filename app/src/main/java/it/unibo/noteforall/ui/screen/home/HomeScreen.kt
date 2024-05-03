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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadHomePosts
import it.unibo.noteforall.ui.composables.LoadingPostsAnimation
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun HomeScreen(navController: NavHostController, db: FirebaseFirestore) {
    var isLaunched by remember { mutableStateOf(false) }
    //var posts by remember { mutableStateOf(mutableListOf<Note>()) }
    val posts = remember { mutableStateListOf<Note>() }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            loadHomePosts(posts, db)
            isLaunched = true
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
            if (posts.size == 0) {
                item { LoadingPostsAnimation() }
            }
            val sortedPosts = posts.sortedBy { it.date }.reversed()
            items(sortedPosts) { post ->
                NoteCard(navController = navController, note = post, db = db)
            }
        }
    }
}
