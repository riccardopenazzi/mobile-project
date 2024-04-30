package it.unibo.noteforall.utils

data class CurrentUser(
    val id: String
)

object CurrentUserSingleton {
    var currentUser: CurrentUser? = null
}