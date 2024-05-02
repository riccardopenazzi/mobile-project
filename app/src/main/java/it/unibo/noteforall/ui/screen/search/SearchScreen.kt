package it.unibo.noteforall.ui.screen.search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.searchPost
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(db: FirebaseFirestore, navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    var isDownloadFinished = remember { AtomicBoolean(false) }
    var posts by remember { mutableStateOf(mutableListOf<Note>()) }

    LaunchedEffect(text) {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                Log.i("debProf", isDownloadFinished.get().toString())
                //Log.i("debDown", "Entro in delay")

                //println("La tua funzione viene chiamata ogni secondo")
                delay(1000)
                //Log.i("debDown", "Esco da delay")

                //isDownloadFinished.set(true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(),
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.Menu, "Filter")
            }
        }
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(2.dp)
                .fillMaxSize()
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                /* Consider to use SearchBar */
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Search") },
                    trailingIcon = {
                        IconButton(onClick = { searchPost(posts, isDownloadFinished, db, text) }) {
                            Icon(Icons.Outlined.Search, "Search")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                )
            }
            if (isDownloadFinished.get()) {
                items(posts) { post ->
                    NoteCard(navController = navController, note = post, db = db)
                }
            }
        }
    }
}
