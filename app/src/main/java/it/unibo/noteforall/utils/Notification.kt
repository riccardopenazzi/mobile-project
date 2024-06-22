package it.unibo.noteforall.utils

import java.util.Date

data class Notification (
    val content: String,
    val idSource: String,
    val idTarget: String,
    val postTarget: String,
    val sourcePicRef: String,
    val isRead: Boolean,
    val date: Date
)