package it.unibo.noteforall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.BottomBar
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.bottomNavigationItems

class MainActivity : ComponentActivity() {
    val items = listOf("Home", "Saved", "Search", "Profile")
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.Star,
        Icons.Outlined.Search,
        Icons.Outlined.Person
    )
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteForAllTheme {
                val items = bottomNavigationItems
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { AppBar(title = "Home") },
                        bottomBar = {
                            BottomBar(
                                items = items,
                                onItemSelected = { index ->
                                    selectedItemIndex = index
                                },
                                selectedItemIndex = selectedItemIndex
                            )
                        }
                    ) { contentPadding ->
                        HomeScreen(modifier = Modifier.padding(contentPadding))
                    }
                }
            }
        }
    }
}