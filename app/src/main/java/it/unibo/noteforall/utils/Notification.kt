package it.unibo.noteforall.utils

data class Notification (
    val content: String,
    val idSource: String,
    val idTarget: String,
    val postTarget: String,
    val sourcePicRef: String,
    val isRead: Boolean,
)