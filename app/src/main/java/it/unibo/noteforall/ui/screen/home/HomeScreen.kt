package it.unibo.noteforall.ui.screen.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import it.unibo.noteforall.ui.composables.NoteCard

@Composable
fun HomeScreen(modifier: Modifier) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(),
                onClick = { /*TODO*/ }
            ) {
                Icon(Icons.Outlined.Add, "New post")
                Text(text = "New post")
            }
        }
    ) { contentPadding ->
        print(contentPadding)
        LazyColumn() {
            for (i in 0..6 ) {
                item { NoteCard() }
            }
        }
    }
}