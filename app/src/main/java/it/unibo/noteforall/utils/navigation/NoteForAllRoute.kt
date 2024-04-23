package it.unibo.noteforall.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.ui.screen.editProfile.EditProfileScreen
import it.unibo.noteforall.ui.screen.home.HomeScreen
import it.unibo.noteforall.ui.screen.myProfile.MyProfileScreen
import it.unibo.noteforall.ui.screen.newNote.NewNoteScreen
import it.unibo.noteforall.ui.screen.saved.SavedNotesScreen
import it.unibo.noteforall.ui.screen.search.SearchScreen
import it.unibo.noteforall.ui.screen.viewNote.ViewNoteScreen

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

    companion object {
        val routes = setOf(Home, Profile, Saved, Search, EditProfile, NewNote, ViewNote)
    }
}

@Composable
fun NoteForAllNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    db: FirebaseFirestore
) {
    NavHost(
        navController = navController,
        startDestination = NoteForAllRoute.Home.route,
        modifier = modifier
    ) {
        with(NoteForAllRoute.Home) {
            composable(route) {

                HomeScreen(navController)
            }
        }
        with(NoteForAllRoute.Profile) {
            composable(route) {
                MyProfileScreen(navController = navController)
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
                EditProfileScreen(db)
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
    }
}
