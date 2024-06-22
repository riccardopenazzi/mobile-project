package it.unibo.noteforall.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.editProfile.EditProfileScreen
import it.unibo.noteforall.ui.screen.editProfile.EditProfileViewModel
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.screen.myProfile.MyProfileScreen
import it.unibo.noteforall.ui.screen.newNote.NewNoteScreen
import it.unibo.noteforall.ui.screen.newNote.NewNoteViewModel
import it.unibo.noteforall.ui.screen.notification.NotificationScreen
import it.unibo.noteforall.ui.screen.profile.ProfileScreen
import it.unibo.noteforall.ui.screen.saved.SavedNotesScreen
import it.unibo.noteforall.ui.screen.search.SearchScreen
import it.unibo.noteforall.ui.screen.settings.SettingsScreen
import it.unibo.noteforall.ui.screen.settings.ThemeState
import it.unibo.noteforall.ui.screen.settings.ThemeViewModel
import it.unibo.noteforall.ui.screen.viewNote.ViewNoteScreen
import it.unibo.noteforall.utils.Note
import org.koin.androidx.compose.koinViewModel

sealed class NoteForAllRoute (
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home: NoteForAllRoute("home", "Home")
    data object MyProfile: NoteForAllRoute("my_profile", "My Profile")
    data object Saved: NoteForAllRoute("saved", "Saved")
    data object Search: NoteForAllRoute("search", "Search")
    data object EditProfile: NoteForAllRoute("edit_profile", "Edit Profile")
    data object NewNote: NoteForAllRoute("new_note", "New Note")
    data object ViewNote: NoteForAllRoute(
        "view_note/{noteId}",
        "Note",
        listOf(navArgument("noteId") { type = NavType.StringType })
    ) {
        fun buildRoute(noteId: String) = "view_note/${noteId}"
    }
    data object Settings: NoteForAllRoute("settings", "Settings")
    data object Profile: NoteForAllRoute(
        "profile/{userId}",
        "Profile",
        listOf(navArgument("userId") { type = NavType.StringType })
    ) {
        fun buildRoute(userId: String) = "profile/${userId}"
    }
    data object Notifications: NoteForAllRoute("notifications", "Notifications")
    companion object {
        val routes = setOf(Home, Saved, Search, MyProfile, EditProfile, NewNote, ViewNote, Profile, Settings, Notifications)
    }
}

@Composable
fun NoteForAllNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    db: FirebaseFirestore,
    internalDb: NoteForAllDatabase,
    state: ThemeState,
    themeVm: ThemeViewModel,
    posts: MutableList<Note>
) {
    NavHost(
        navController = navController,
        startDestination = NoteForAllRoute.Home.route,
        modifier = modifier
    ) {
        with(NoteForAllRoute.Home) {
            composable(route) {
                HomeScreen(navController, db, posts)
            }
        }
        with(NoteForAllRoute.MyProfile) {
            composable(route) {
                MyProfileScreen(navController = navController, db)
            }
        }
        with(NoteForAllRoute.Saved) {
            composable(route) {
                SavedNotesScreen(navController = navController, db)
            }
        }
        with(NoteForAllRoute.Search) {
            composable(route) {
                SearchScreen(db, navController, posts)
            }
        }
        with(NoteForAllRoute.EditProfile) {
            composable(route) {
                val editProfileVm = koinViewModel<EditProfileViewModel>()
                val editProfileState by editProfileVm.state.collectAsStateWithLifecycle()
                EditProfileScreen(db, editProfileState, editProfileVm.actions, navController)
            }
        }
        with(NoteForAllRoute.NewNote) {
            composable(route) {
                val newNoteVm = koinViewModel<NewNoteViewModel>()
                val newNoteState by newNoteVm.state.collectAsStateWithLifecycle()
                NewNoteScreen(newNoteState, newNoteVm.actions, navController)
            }
        }
        with(NoteForAllRoute.ViewNote) {
            composable(route, arguments) {backStackEntry ->
                val id = backStackEntry.arguments?.getString("noteId")!!
                ViewNoteScreen(navController, id, db)
            }
        }
        with(NoteForAllRoute.Settings) {
            composable(route) {
                SettingsScreen(internalDb, state, themeVm)
            }
        }
        with(NoteForAllRoute.Profile) {
            composable(route, arguments) {backStackEntry ->
                val id = backStackEntry.arguments?.getString("userId")!!
                ProfileScreen(navController = navController, userId = id, db = db)
            }
        }
        with(NoteForAllRoute.Notifications) {
            composable(route) {
                NotificationScreen(navController)
            }
        }
    }
}
