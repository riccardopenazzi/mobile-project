package it.unibo.noteforall.utils.navigation

sealed class Screens (val screen: String) {
    data object Home: Screens("home")
    data object Profile: Screens("profile")
    data object Saved: Screens("saved")
    data object Search: Screens("search")
    data object EditProfile: Screens("edit_profile")
}