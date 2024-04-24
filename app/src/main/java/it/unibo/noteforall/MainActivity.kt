package it.unibo.noteforall

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.data.NoteForAllDatabase
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.NavigationBar
import it.unibo.noteforall.ui.screen.login.LoginScreen
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.navigation.bottomNavigationItems
import it.unibo.noteforall.utils.navigation.NoteForAllNavGraph
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    private val internalDb by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteForAllDatabase::class.java,
            "noteforall.db"
        ).build()
    }

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
                var isLogged by remember {
                    mutableStateOf(false)
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
//---------------------------------------------------------------------------------------------
                    LaunchedEffect(Unit) {
                        val userDao = internalDb.dao
                        CoroutineScope(Dispatchers.IO).launch {
                            val userId = userDao.getUserId()
                            withContext(Dispatchers.Main) {
                                if (userId != null) {
                                    isLogged = true;
                                }
                            }
                        }
                    }
//---------------------------------------------------------------------------------------------
                    if (!isLogged) {
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
                    } else {
                        LoginScreen(db = db)
                    }
                }
            }
        }
    }
}
