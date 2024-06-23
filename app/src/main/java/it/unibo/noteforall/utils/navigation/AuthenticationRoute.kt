package it.unibo.noteforall.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.login.LoginScreen
import it.unibo.noteforall.ui.screen.signup.SignupScreen
import it.unibo.noteforall.ui.screen.signup.SignupViewModel
import org.koin.androidx.compose.koinViewModel

sealed class AuthenticationRoute (
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
    internalDb: NoteForAllDatabase
) {
    NavHost(
        navController = navController,
        startDestination = AuthenticationRoute.Login.route
    ) {
        with(AuthenticationRoute.Login) {
            composable(route) {
                LoginScreen(navController, internalDb)
            }
        }
        with(AuthenticationRoute.Signup) {
            composable(route) {
                val signupVm = koinViewModel<SignupViewModel>()
                val state by signupVm.state.collectAsStateWithLifecycle()
                SignupScreen(navController, internalDb, state, signupVm.actions)
            }
        }
    }
}
