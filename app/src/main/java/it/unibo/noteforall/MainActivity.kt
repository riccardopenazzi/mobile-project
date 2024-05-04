package it.unibo.noteforall

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.ui.composables.AppBar
import it.unibo.noteforall.ui.composables.NavigationBar
import it.unibo.noteforall.ui.screen.settings.Theme
import it.unibo.noteforall.ui.screen.settings.ThemeViewModel
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.navigation.NoteForAllNavGraph
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import it.unibo.noteforall.utils.navigation.bottomNavigationItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.atomic.AtomicInteger

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
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        setContent {
            val themeVm = koinViewModel<ThemeViewModel>()
            val state by themeVm.state.collectAsStateWithLifecycle()

            NoteForAllTheme(
                darkTheme = when(state.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                val items = bottomNavigationItems
                val selectedItemIndex by rememberSaveable {
                    mutableStateOf(AtomicInteger(0))
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
                            val user = userDao.getUserId()
                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    isLogged = true;
                                    val currentUser = CurrentUser(
                                        id = user.userId
                                    )
                                    CurrentUserSingleton.currentUser = currentUser
                                }
                            }
                        }
                    }
//---------------------------------------------------------------------------------------------
                    Scaffold(
                        topBar = { AppBar(navigationController, currentRoute) },
                        bottomBar = {
                            if (items.any { it.title == currentRoute.title }) {
                                NavigationBar(
                                    navController = navigationController,
                                    items = items,
                                    onItemSelected = { index ->
                                        selectedItemIndex.set(index)
                                    },
                                    selectedItemIndex = selectedItemIndex,
                                    currentRoute = currentRoute
                                )
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ) { contentPadding ->
                        NoteForAllNavGraph(
                            navController = navigationController,
                            modifier = Modifier.padding(contentPadding),
                            db,
                            internalDb,
                            state,
                            themeVm
                        )
                    }
                }
            }
        }
    }
}
