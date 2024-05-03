package it.unibo.noteforall.data.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class StorageUtil {

    companion object {

        suspend fun createPost(
            imageUri: Uri,
            noteUri: Uri,
            context: Context,
            post: HashMap<String, String>,
            navController: NavHostController
        ) {
            post["pic_ref"] = uploadToStorage(imageUri, context, "posts_pic", "jpg")
            post["note_ref"] = uploadToStorage(noteUri, context, "posts_note", "pdf")
            val postRef = FirebaseFirestore.getInstance().collection("posts")
            postRef.add(post).addOnSuccessListener { newPost ->
                val addDate = hashMapOf(
                    "date" to Timestamp.now()
                )
                postRef.document(newPost.id).update(addDate as Map<String, Any>)
                    .addOnSuccessListener {
                        navController.navigate(NoteForAllRoute.Home.route)
                    }
            }.addOnFailureListener {
                //TO DO MANAGE POST CREATION
            }
        }

        private suspend fun uploadToStorage(
            uri: Uri,
            context: Context,
            destinationBucket: String,
            extension: String
        ): String {
            return suspendCoroutine { continuation ->
                val storageRef = Firebase.storage.reference
                val uniqueDocumentName = UUID.randomUUID()
                val spaceRef = storageRef.child("$destinationBucket/$uniqueDocumentName.$extension")
                val documentByteArray: ByteArray? = context.contentResolver
                    .openInputStream(uri)
                    ?.use { it.readBytes() }
                documentByteArray?.let {
                    spaceRef.putBytes(documentByteArray)
                        .addOnSuccessListener {
                            val documentPosition =
                                "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/$destinationBucket%2F$uniqueDocumentName.$extension?alt=media"
                            continuation.resume(documentPosition)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } ?: run {
                    // Handle case where imageByteArray is null
                    continuation.resumeWithException(NullPointerException("Document byte array is null"))
                }
            }
        }

        suspend fun updateUserInfo(
            imageUri: Uri? = null,
            context: Context,
            name: String,
            surname: String,
            username: String,
            oldPassword: String,
            newPassword: String,
            repeatNewPassword: String,
            navController: NavHostController
        ) {
            val userUpdate = hashMapOf(
                "name" to name,
                "surname" to surname,
                "username" to username
            )
            //change pic if necessary
            if (imageUri != null) {
                val picPosition = uploadToStorage(imageUri, context, "users_pic", "jpg")
                userUpdate["user_pic"] = picPosition
            }
            //change password if necessary
            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && repeatNewPassword.isNotEmpty()) {
                if (newPassword == repeatNewPassword) {
                    if (checkPassword(oldPassword)) {
                        userUpdate["password"] = newPassword
                    } else {
                        //error old password don't correspond
                    }
                }
            } else {
                //error new passwords don't match
            }
            FirebaseFirestore.getInstance().collection("users")
                .document(CurrentUserSingleton.currentUser!!.id).update(
                    userUpdate as Map<String, Any>
                ).addOnSuccessListener {
                    navController.navigate(NoteForAllRoute.MyProfile.route)
                }
        }

        private suspend fun checkPassword(oldPassword: String): Boolean {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users")
                    .document(CurrentUserSingleton.currentUser!!.id).get()
                    .addOnSuccessListener { user ->
                        val oldPasswordDb = user.getString("password")
                        if (oldPasswordDb == oldPassword) {
                            continuation.resume(true)
                        }
                    }.addOnFailureListener {
                        continuation.resume(false)
                    }
            }
        }

        private suspend fun isPostSaved(postId: String): Boolean {
            return suspendCoroutine { continuation ->
                val savedPostsRef = FirebaseFirestore.getInstance().collection("users")
                    .document(CurrentUserSingleton.currentUser!!.id).collection("saved_posts")
                savedPostsRef.whereEqualTo("post_id", postId).get().addOnSuccessListener { res ->
                    Log.i("debSave", "Analizzo $postId e ritorno ${!res.isEmpty}")
                    continuation.resume(!res.isEmpty)
                }.addOnFailureListener { exception ->
                    Log.i("debSave", "Errore $exception")
                }
            }
        }

        fun savePost(postId: String, db: FirebaseFirestore) {
            val time = Timestamp.now()
            val savedPost = hashMapOf(
                "post_id" to postId,
                "saved_date" to time

            )
            db.collection("users").document(CurrentUserSingleton.currentUser!!.id)
                .collection("saved_posts")
                .add(savedPost)
        }

        fun unsavePost(postId: String, db: FirebaseFirestore) {
            db.collection("users").document(CurrentUserSingleton.currentUser!!.id)
                .collection("saved_posts")
                .whereEqualTo("post_id", postId).get().addOnSuccessListener { post ->
                    if (!post.isEmpty) {
                        Log.i("deb", "Post non è empty")
                        post.documents.first().reference.delete()
                    }
                }
        }

        suspend fun getAllPosts(): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("posts").get()
                    .addOnSuccessListener { posts ->
                        continuation.resume(posts)
                    }
            }
        }

        suspend fun loadHomePosts(
            noteList: MutableList<Note>,
            db: FirebaseFirestore
        ) {
            val allPosts = getAllPosts()
            for (post in allPosts) {
                val isSaved = isPostSaved(post.id)
                noteList.add(
                    Note(
                        postId = post.id,
                        isSaved = isSaved,
                        title = post.getString("title"),
                        description = post.getString("description"),
                        category = post.getString("category"),
                        picRef = post.getString("pic_ref"),
                        noteRef = post.getString("note_ref"),
                        author = post.getString("user_id")
                            ?.let { getUserSingleInfo(it, "username") },
                        authorPicRef = post.getString("user_id")
                            ?.let { getUserSingleInfo(it, "user_pic") },
                        userId = post.getString("user_id")!!,
                        date = post.getTimestamp("date")!!
                    )
                )
            }

        }

        suspend fun getPostFromId(noteId: String): DocumentSnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("posts").document(noteId).get()
                    .addOnSuccessListener { post ->
                        continuation.resume(post)
                    }
            }
        }

        suspend fun getUserPosts(userId: String): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("posts").whereEqualTo("user_id", userId)
                    .get()
                    .addOnSuccessListener { allUserPosts ->
                        continuation.resume(allUserPosts)
                    }
            }
        }

        private suspend fun getUserSingleInfo(userId: String, infoName: String): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                    .addOnSuccessListener { user ->
                        user.getString(infoName)?.let {
                            continuation.resume(it)
                        }
                    }
            }
        }

        suspend fun loadNote(
            noteId: String,
            db: FirebaseFirestore,
            posts: MutableList<Note>
        ) {
            val post = getPostFromId(noteId)
            val isSaved = isPostSaved(noteId)
            posts.add(
                Note(
                    postId = post.id,
                    isSaved = isSaved,
                    title = post.getString("title"),
                    description = post.getString("description"),
                    category = post.getString("category"),
                    picRef = post.getString("pic_ref"),
                    noteRef = post.getString("note_ref"),
                    author = post.getString("user_id")?.let { getUserSingleInfo(it, "username") },
                    authorPicRef = post.getString("user_id")
                        ?.let { getUserSingleInfo(it, "user_pic") },
                    userId = post.getString("user_id")!!,
                    date = post.getTimestamp("note")
                )
            )
        }

        suspend fun getUserSavedPosts(): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users")
                    .document(CurrentUserSingleton.currentUser!!.id)
                    .collection("saved_posts").get().addOnSuccessListener { savedPosts ->
                        continuation.resume(savedPosts)
                    }
            }
        }

        suspend fun loadSavedPosts(
            noteList: MutableList<Note>,
            db: FirebaseFirestore
        ) {
            val savedPosts = getUserSavedPosts()
            for (postId in savedPosts) {
                val post = postId.getString("post_id")?.let { getPostFromId(it) }
                if (post != null) {
                    noteList.add(
                        Note(
                            postId = post.id,
                            isSaved = true,
                            title = post.getString("title"),
                            description = post.getString("description"),
                            category = post.getString("category"),
                            picRef = post.getString("pic_ref"),
                            noteRef = post.getString("note_ref"),
                            author = post.getString("user_id")?.let { getUserSingleInfo(it, "username") },
                            authorPicRef = post.getString("user_id")
                                ?.let { getUserSingleInfo(it, "user_pic") },
                            userId = post.getString("user_id")!!,
                            date = post.getTimestamp("date"),
                            savedDate = postId.getTimestamp("saved_date")
                        )
                    )
                }
            }
        }

        suspend fun loadUserPosts(
            noteList: MutableList<Note>,
            db: FirebaseFirestore,
            userId: String
        ) {
            val allUserPosts = getUserPosts(userId)
            for (post in allUserPosts) {
                val isSaved = isPostSaved(post.id)
                Log.i("debSave", isSaved.toString())
                val postOwnerUserId = post.getString("user_id")
                if (postOwnerUserId != null) {
                    noteList.add(
                        Note(
                            postId = post.id,
                            isSaved = isSaved,
                            title = post.getString("title"),
                            description = post.getString("description"),
                            category = post.getString("category"),
                            picRef = post.getString("pic_ref"),
                            noteRef = post.getString("note_ref"),
                            author = getUserSingleInfo(postOwnerUserId, "username"),
                            authorPicRef = getUserSingleInfo(postOwnerUserId, "user_pic"),
                            userId = post.getString("user_id")!!,
                            date = post.getTimestamp("date")
                        )
                    )
                }
            }
        }

        fun searchPost(
            noteList: MutableList<Note>,
            db: FirebaseFirestore,
            key: String
        ) {
            db.collection("posts").get().addOnSuccessListener { posts ->
                for (post in posts) {
                    if (post.getString("title")?.contains(key)!! || post.getString("description")
                            ?.contains(key)!! || post.getString("category")?.contains(key)!!
                    ) {
                        val userId = post.getString("user_id")
                        if (userId != null) {
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { user ->
                                    val savedPostsRef = db.collection("users")
                                        .document(CurrentUserSingleton.currentUser!!.id)
                                        .collection("saved_posts")
                                    savedPostsRef.whereEqualTo("post_id", post.id).get()
                                        .addOnSuccessListener { res ->
                                            Log.i("debHome", "Aggiungo in lista")
                                            noteList.add(
                                                Note(
                                                    postId = post.id,
                                                    isSaved = !res.isEmpty,
                                                    title = post.getString("title"),
                                                    description = post.getString("description"),
                                                    category = post.getString("category"),
                                                    picRef = post.getString("pic_ref"),
                                                    noteRef = post.getString("note_ref"),
                                                    author = user.getString("username"),
                                                    authorPicRef = user.getString("user_pic"),
                                                    userId = post.getString("user_id")!!,
                                                    date = post.getTimestamp("date")
                                                )
                                            )
                                        }
                                }
                        }
                    }
                }
            }
        }

        fun downloadNote(ref: String, ctx: Context) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ref))
            ctx.startActivity(intent)
        }

        fun getCategoriesList(categoriesList: MutableList<String>) {
            FirebaseFirestore.getInstance().collection("posts").get()
                .addOnSuccessListener { allPosts ->
                    for (post in allPosts) {
                        Log.i("debCat", categoriesList.toString())
                        val currentCategory = post.getString("category")
                        if (currentCategory != null && !categoriesList.contains(currentCategory)) {
                            categoriesList.add(currentCategory)
                        }
                    }
                }
        }

        fun applyFilters(
            postsToFilter: MutableList<Note>,
            selectedCategory: String,
            ascending: Boolean,
            descending: Boolean,
        ) {
            if (selectedCategory.isNotEmpty()) {
                for (post in postsToFilter) {
                    if (post.category != selectedCategory) {
                        postsToFilter.remove(post)
                    }
                }
            }

        }
    }
}