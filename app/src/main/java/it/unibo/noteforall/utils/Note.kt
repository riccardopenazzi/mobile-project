package it.unibo.noteforall.utils

data class Note(
    val postId: String?,
    val isSaved: Boolean,
    val title: String?,
    val description: String?,
    val category: String?,
    val picRef: String?,
    val noteRef: String?,
    val author: String?,
    val authorPicRef: String?
)