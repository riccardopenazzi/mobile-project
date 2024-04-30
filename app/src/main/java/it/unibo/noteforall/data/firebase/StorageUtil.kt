package it.unibo.noteforall.data.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.unibo.noteforall.utils.CurrentUserSingleton
import java.util.UUID

class StorageUtil {


    companion object {

        fun uploadToStorage(
            imageUri: Uri,
            context: Context,
            type: String,
            location: String,
            post: HashMap<String, String>? = null,
            noteUri: Uri? = null
        ) {
            val storage = Firebase.storage
            val storageRef = storage.reference
            val uniqueImageName = UUID.randomUUID()
            val spaceRef = when (location) {
                "post_pic" -> storageRef.child("posts_pic/$uniqueImageName.jpg")
                else -> {
                    storageRef.child("users_pic/$uniqueImageName.jpg")
                }
            }
            val byteArray: ByteArray? = context.contentResolver
                .openInputStream(imageUri)
                ?.use { it.readBytes() }
            byteArray?.let {
                val uploadTask = spaceRef.putBytes(byteArray)
                uploadTask.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "upload failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnSuccessListener {
                    val userRef = Firebase.firestore.collection("users").document(
                        CurrentUserSingleton.currentUser!!.id
                    )
                    if (location == "user_pic") {
                        val picPosition =
                            "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/users_pic%2F$uniqueImageName.jpg?alt=media"
                        userRef.update("user_pic", picPosition)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "User profile picture updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Error updating user profile picture: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.message?.let { it1 -> Log.i("debImg", it1) }
                            }
                    } else {

                        if (noteUri != null) {
                            val uniqueNoteName = UUID.randomUUID()
                            val noteSpaceRef = storageRef.child("posts_note/$uniqueNoteName.pdf")
                            val noteByteArray: ByteArray? = context.contentResolver
                                .openInputStream(noteUri)
                                ?.use { it.readBytes() }
                            noteByteArray?.let {
                                val uploadNote =
                                    noteSpaceRef.putBytes(noteByteArray).addOnSuccessListener {
                                        val notePosition =
                                            "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/posts_note%2F$uniqueNoteName.pdf?alt=media"
                                        post?.put("noteRef", notePosition)
                                        val picPosition =
                                            "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/posts_pic%2F$uniqueImageName.jpg?alt=media"
                                        post?.put("picRef", picPosition)
                                        if (post != null) {
                                            userRef.collection("posts").add(post).addOnSuccessListener {
                                                Log.i("debPdf", "Post caricato in teoria")
                                            }
                                        }
                                    }
                            }
                        } else {
                            Log.i("debPdf", "Il note uri è null")
                        }
                    }
                }
            }


        }

    }
}