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

        fun uploadToStorage(uri: Uri, context: Context, type: String) {
            val storage = Firebase.storage

            // Create a storage reference from our app
            var storageRef = storage.reference

            val unique_image_name = UUID.randomUUID()
            var spaceRef: StorageReference


            spaceRef = storageRef.child("users_pic/$unique_image_name.jpg")


            val byteArray: ByteArray? = context.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }

            byteArray?.let {

                var uploadTask = spaceRef.putBytes(byteArray)
                uploadTask.addOnFailureListener {
                    Toast.makeText(
                        context,
                        "upload failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    val userRef = Firebase.firestore.collection("users").document(
                        CurrentUserSingleton.currentUser!!.id)
                    val picPosition =
                        "gs://noteforall-2f581.appspot.com/users_pic/$unique_image_name.jpg"
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
                }
            }


        }

    }
}