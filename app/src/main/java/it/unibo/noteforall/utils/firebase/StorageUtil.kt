package it.unibo.noteforall.utils.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.unibo.noteforall.utils.CurrentUserSingleton
import java.util.UUID

class StorageUtil {


    companion object {

        fun uploadToStorage(uri: Uri, context: Context, type: String, location: String, post: HashMap<String, String>? = null) {
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
                .openInputStream(uri)
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
                        val picPosition =
                            "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/posts_pic%2F$uniqueImageName.jpg?alt=media"
                        post?.put("picRef", picPosition)
                        if (post != null) {
                            userRef.collection("posts").add(post)
                        }
                    }
                }
            }


        }

    }
}