package it.unibo.noteforall.utils

data class CurrentUser(
    val id: String,
    val key: String //it could be emil or username
)

object CurrentUserSingleton {
    var currentUser: CurrentUser? = null
}