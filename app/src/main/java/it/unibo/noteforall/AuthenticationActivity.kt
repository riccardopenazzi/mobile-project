package it.unibo.noteforall

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.wear.compose.material.Scaffold
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.settings.Theme
import it.unibo.noteforall.ui.screen.settings.ThemeViewModel
import it.unibo.noteforall.ui.theme.NoteForAllTheme
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.navigation.AuthenticationNavGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

class AuthenticationActivity : ComponentActivity() {
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
                darkTheme = when (state.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                var isLogged by remember {
                    mutableStateOf(false)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()

                    LaunchedEffect(Unit) {
                        val userDao = internalDb.dao
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = userDao.getUserId()
                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    isLogged = true
                                    val currentUser = CurrentUser(
                                        id = user.userId
                                    )
                                    CurrentUserSingleton.currentUser = currentUser
                                }
                            }
                        }
                    }

                    Scaffold {
                        val ctx = LocalContext.current

                        if (!isLogged) {
                            AuthenticationNavGraph(
                                navController = navigationController,
                                internalDb = internalDb
                            )
                        } else {
                            val intent = Intent(ctx, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            ctx.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}