package it.unibo.noteforall.ui.screen.search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.searchPost
import it.unibo.noteforall.ui.composables.LoadingAnimation
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.ui.composables.outlinedTextFieldColors
import it.unibo.noteforall.utils.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(db: FirebaseFirestore, navController: NavHostController, posts: MutableList<Note>) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isLaunched by remember { mutableStateOf(false) }
    var isSearched by remember { mutableStateOf(false) }
    var isListUpdated by remember { mutableStateOf(false) }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            posts.clear()
            isLaunched = true
        }
    }

    Scaffold { contentPadding ->
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
                        IconButton(onClick = {
                            posts.clear()
                            //searchPost(posts, db, text)
                        }) {
                            Icon(Icons.Outlined.Search, "Search")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        posts.clear()
                        isSearched = true
                        isListUpdated = true
                        CoroutineScope(Dispatchers.Main).launch {
                            searchPost(posts, db, text)
                        }
                        focusManager.clearFocus()
                    }),
                    colors = outlinedTextFieldColors()
                )
            }
            if (isSearched && posts.size == 0) {
                item { LoadingAnimation() }
            } else {
                if (isListUpdated) {
                    items(posts) { post ->
                        if (isSearched) {
                            isSearched = false
                        }
                        NoteCard(navController = navController, note = post, db = db)
                    }
                }
            }
        }
    }
}
