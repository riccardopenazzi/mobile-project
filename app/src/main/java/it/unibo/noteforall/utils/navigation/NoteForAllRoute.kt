package it.unibo.noteforall.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.editProfile.EditProfileActions
import it.unibo.noteforall.ui.screen.editProfile.EditProfileScreen
//import it.unibo.noteforall.ui.screen.editProfile.EditProfileViewModel
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.screen.login.LoginScreen
import it.unibo.noteforall.ui.screen.myProfile.MyProfileScreen
import it.unibo.noteforall.ui.screen.newNote.NewNoteScreen
import it.unibo.noteforall.ui.screen.saved.SavedNotesScreen
import it.unibo.noteforall.ui.screen.search.SearchScreen
import it.unibo.noteforall.ui.screen.signup.SignupScreen
import it.unibo.noteforall.ui.screen.viewNote.ViewNoteScreen
import org.koin.androidx.compose.koinViewModel

sealed class NoteForAllRoute (
    val route: String,
    val title: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object Home: NoteForAllRoute("home", "Home")
    data object Profile: NoteForAllRoute("my_profile", "My Profile")
    data object Saved: NoteForAllRoute("saved", "Saved")
    data object Search: NoteForAllRoute("search", "Search")
    data object EditProfile: NoteForAllRoute("edit_profile", "Edit Profile")
    data object NewNote: NoteForAllRoute("new_note", "New Note")
    data object ViewNote: NoteForAllRoute("view_note", "Note")
    data object Login: NoteForAllRoute("login", "Login")
    data object Signup: NoteForAllRoute("signup", "Signup")
    companion object {
        val routes = setOf(Home, Profile, Saved, Search, EditProfile, NewNote, ViewNote, Login, Signup)
    }
}

@Composable
fun NoteForAllNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    db: FirebaseFirestore,
    isLogged: Boolean,
    internalDb: NoteForAllDatabase
) {
    NavHost(
        navController = navController,
        startDestination = if (isLogged) NoteForAllRoute.Home.route else NoteForAllRoute.Login.route,
        modifier = modifier
    ) {
        with(NoteForAllRoute.Home) {
            composable(route) {
                HomeScreen(navController)
            }
        }
        with(NoteForAllRoute.Profile) {
            composable(route) {
                MyProfileScreen(navController = navController, db)
            }
        }
        with(NoteForAllRoute.Saved) {
            composable(route) {
                SavedNotesScreen(navController = navController)
            }
        }
        with(NoteForAllRoute.Search) {
            composable(route) {
                SearchScreen()
            }
        }
        with(NoteForAllRoute.EditProfile) {
            composable(route) {
                val editProfileVm = koinViewModel<EditProfileActions.EditProfileViewModel>()
                val state by editProfileVm.state.collectAsStateWithLifecycle()
                EditProfileScreen()
            }
        }
        with(NoteForAllRoute.NewNote) {
            composable(route) {
                NewNoteScreen()
            }
        }
        with(NoteForAllRoute.ViewNote) {
            composable(route) {
                ViewNoteScreen(navController)
            }
        }
        with(NoteForAllRoute.Login) {
            composable(route) {
                LoginScreen(db, navController, internalDb)
            }
        }
        with(NoteForAllRoute.Signup) {
            composable(route) {
                SignupScreen(db, navController, internalDb)
            }
        }
    }
}
