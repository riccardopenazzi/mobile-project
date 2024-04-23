package it.unibo.noteforall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.NavigationBar
import it.unibo.noteforall.ui.screen.editProfile.EditProfileScreen
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.screen.myProfile.MyProfileScreen
import it.unibo.noteforall.ui.screen.saved.SavedNotesScreen
import it.unibo.noteforall.ui.screen.search.SearchScreen
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.bottomNavigationItems
import it.unibo.noteforall.utils.navigation.NoteForAllNavGraph
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        //Added just to simulate a logged user---------------------
        val currentUser = CurrentUser(
            id = "IzLnDGab6LfPTBPrKE9I",
            key = ""
        )
        CurrentUserSingleton.currentUser = currentUser
        //---------------------------------------------------------
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
                    val navigationController = rememberNavController()
                    val backStackEntry by navigationController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            NoteForAllRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: NoteForAllRoute.Home
                        }
                    }

                    Scaffold(
                        topBar = { AppBar(navigationController, currentRoute) },
                        bottomBar = {
                            if (items.any { it.title == currentRoute.title }) {
                                NavigationBar(
                                    navController = navigationController,
                                    items = items,
                                    onItemSelected = { index ->
                                        selectedItemIndex = index
                                    },
                                    selectedItemIndex = selectedItemIndex
                                )
                            }
                        }
                    ) { contentPadding ->
                        NoteForAllNavGraph(
                            navController = navigationController,
                            modifier = Modifier.padding(contentPadding),
                            db
                        )
                    }
                }
            }
        }
    }
}
