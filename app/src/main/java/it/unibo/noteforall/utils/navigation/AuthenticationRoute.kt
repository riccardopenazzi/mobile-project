package it.unibo.noteforall.utils.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.login.LoginScreen
import it.unibo.noteforall.ui.screen.signup.SignupScreen

sealed class AuthenticationRoute (
    val route: String,
    val title: String
) {
    data object Login: NoteForAllRoute("login", "Login")
    data object Signup: NoteForAllRoute("signup", "Signup")

    companion object {
        val routes = setOf(Login, Signup)
    }
}

@Composable
fun AuthenticationNavGraph(
    navController: NavHostController,
    db: FirebaseFirestore,
    internalDb: NoteForAllDatabase
) {
    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute.Login.route
    ) {
        with(AuthenticationRoute.Login) {
            composable(route) {
                LoginScreen(db, navController, internalDb)
            }
        }
        with(AuthenticationRoute.Signup) {
            composable(route) {
                SignupScreen(db, navController, internalDb)
            }
        }
    }
}
