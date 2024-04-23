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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.BottomBar
import it.unibo.noteforall.ui.screen.editProfile.EditProfileScreen
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.screen.myProfile.MyProfileScreen
import it.unibo.noteforall.ui.screen.saved.SavedNotesScreen
import it.unibo.noteforall.ui.screen.search.SearchScreen
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.bottomNavigationItems
import it.unibo.noteforall.utils.navigation.Screens

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore
    private val isNavbarVisible = mutableStateOf(true)

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
                    /*Scaffold(
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
                    }*/
                    CustomNavigationBar(isNavbarVisible, db)
                }
            }
        }
    }
}

@Composable
fun CustomNavigationBar(isNavbarVisibile: MutableState<Boolean>, db: FirebaseFirestore) {
    val navigationController = rememberNavController()
    val selected = remember {
        mutableStateOf(Icons.Filled.Home)
    }

    Scaffold(
        bottomBar = {
            if (isNavbarVisibile.value) {

                BottomAppBar {
                    //Home
                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.Home
                            navigationController.navigate(Screens.Home.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.Home) Color.White else Color.Black
                        )
                    }
                    //Saved
                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.Star
                            navigationController.navigate(Screens.Saved.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.Star) Color.White else Color.Black
                        )
                    }
                    //Search
                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.Search
                            navigationController.navigate(Screens.Search.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.Search) Color.White else Color.Black
                        )
                    }
                    //Home
                    IconButton(
                        onClick = {
                            selected.value = Icons.Default.Person
                            navigationController.navigate(Screens.Profile.screen) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = if (selected.value == Icons.Default.Person) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = Screens.Home.screen,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screens.Home.screen) {
                isNavbarVisibile.value = true
                HomeScreen(modifier = Modifier.padding(paddingValues))
            }
            composable(Screens.Saved.screen) {
                isNavbarVisibile.value = true
                SavedNotesScreen()
            }
            composable(Screens.Search.screen) {
                isNavbarVisibile.value = true
                SearchScreen()
            }
            composable(Screens.Profile.screen) {
                isNavbarVisibile.value = true
                MyProfileScreen(navigationController)
            }
            composable(Screens.EditProfile.screen) {
                isNavbarVisibile.value = false
                EditProfileScreen(navigationController, db)
            }
        }

    }
}