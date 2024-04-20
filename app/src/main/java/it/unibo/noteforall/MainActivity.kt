package it.unibo.noteforall

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.BottomBar
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

    @OptIn(ExperimentalMaterial3Api::class)
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
                        println(contentPadding)
                        Spacer(modifier = Modifier.padding(200.dp))
                        Greeting("Android")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteForAllTheme {
        Greeting("Android")
    }
}