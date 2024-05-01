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
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun HomeScreen(navController: NavHostController, db: FirebaseFirestore) {
    var isLaunched by remember { mutableStateOf(false) }
    var isDownloadFinished = remember { AtomicBoolean(false) }
    var posts by remember { mutableStateOf(mutableListOf<Note>()) }

    fun loadPosts(
        noteList: MutableList<Note>,
        flag: AtomicBoolean,
        db: FirebaseFirestore
    ) {
        var requestCounter = 0
        db.collection("users").get().addOnSuccessListener { users ->
            requestCounter = users.size()
            for (user in users) {
                val username = user.getString("username")
                val userPicRef = user.getString("user_pic")
                db.collection("users").document(user.id).collection("posts").get()
                    .addOnSuccessListener { userPosts ->
                        requestCounter--
                        for (post in userPosts) {
                            var saved = false
                            val savedPostsRef = db.collection("users")
                                .document(CurrentUserSingleton.currentUser!!.id)
                                .collection("saved_posts")
                            savedPostsRef.whereEqualTo("post_id", post.id).get()
                                .addOnSuccessListener { res ->
                                    noteList.add(
                                        Note(
                                            postId = post.id,
                                            isSaved = !res.isEmpty,
                                            title = post.getString("title"),
                                            description = post.getString("description"),
                                            category = post.getString("category"),
                                            picRef = post.getString("picRef"),
                                            noteRef = post.getString("description"),
                                            author = username,
                                            authorPicRef = userPicRef
                                        )
                                    )
                                    if (requestCounter == 0) {
                                        Log.i("debHome", "fine caricamento")
                                        isDownloadFinished.set(true)
                                    }
                                }
                        }
                    }
            }
        }
    }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            loadPosts(posts, isDownloadFinished, db)
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
            Log.i("debHome", "Valuto condizione per if")
            if (isDownloadFinished.get()) {
                items(posts) { post ->
                    NoteCard(navController = navController, note = post, db = db)
                }
            }
        }
    }
}
