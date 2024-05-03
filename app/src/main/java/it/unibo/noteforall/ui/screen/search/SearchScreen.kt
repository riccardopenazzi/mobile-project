package it.unibo.noteforall.ui.screen.search

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
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import it.unibo.noteforall.ui.composables.FiltersDialog
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Note

@Composable
fun SearchScreen(db: FirebaseFirestore, navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    val posts = remember { mutableStateListOf<Note>() }
    var showFiltersDialog by remember { mutableStateOf(false) }
    val categories = remember { mutableStateListOf<String>() }
    val focusManager = LocalFocusManager.current

    StorageUtil.getCategoriesList(categories)

    if (showFiltersDialog) {
        FiltersDialog(
            categories = categories,
            onDismiss = { showFiltersDialog = false },
            onConfirm = { showFiltersDialog = false },
            posts
        )
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                contentColor = MaterialTheme.colorScheme.primary,
                onClick = { showFiltersDialog = true}
            ) {
                Icon(Icons.Outlined.Menu, "Filters")
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
                        IconButton(onClick = {
                            posts.clear()
                            searchPost(posts, db, text)
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
                        searchPost(posts, db, text)
                        focusManager.clearFocus()
                    })
                )
            }
            items(posts) { post ->
                NoteCard(navController = navController, note = post, db = db)
            }
        }
    }
}
