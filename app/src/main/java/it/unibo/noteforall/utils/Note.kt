package it.unibo.noteforall.utils

import com.google.firebase.Timestamp

data class Note(
    val postId: String? = null,
    val isSaved: Boolean = false,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
    val picRef: String? = null,
    val noteRef: String? = null,
    val author: String? = null,
    val authorPicRef: String? = null,
    val userId: String,
    val date: Timestamp?,
    val savedDate: Timestamp? = null
)