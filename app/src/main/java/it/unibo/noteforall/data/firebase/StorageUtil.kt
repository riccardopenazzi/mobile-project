package it.unibo.noteforall.data.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.unibo.noteforall.MainActivity
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.database.User
import it.unibo.noteforall.utils.Badge
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
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
            if (uploadPost(post)) {
                if (!checkUserObtainedGamificationPosts(1)) {
                    if (checkGamificationPosts(1)) {
                        addObtainedGamificationPost(1)
                    }
                }
                if (!checkUserObtainedGamificationPosts(5)) {
                    if (checkGamificationPosts(5)) {
                        addObtainedGamificationPost(5)
                    }
                }
                navController.navigate(NoteForAllRoute.Home.route)
            } else {
                //ERROR UPLOADING POST
            }
            //gamification
        }

        private suspend fun uploadPost(post: HashMap<String, String>): Boolean {
            return suspendCoroutine { continuation ->
                val postRef = FirebaseFirestore.getInstance().collection("posts")
                postRef.add(post).addOnSuccessListener { newPost ->
                    val addDate = hashMapOf(
                        "date" to Timestamp.now()
                    )
                    postRef.document(newPost.id).update(addDate as Map<String, Any>)
                        .addOnSuccessListener {
                            continuation.resume(true)
                        }
                }.addOnFailureListener {
                    continuation.resume(false)
                }
            }
        }

        private suspend fun checkGamificationPosts(threshold: Int): Boolean {
            return getUserPosts(CurrentUserSingleton.currentUser!!.id).size() == threshold
        }

        private suspend fun checkUserObtainedGamificationPosts(threshold: Int): Boolean {
            val idList = getUserBadgesId(CurrentUserSingleton.currentUser!!.id)
            val searchedId = when (threshold) {
                1 -> "D2tj9ImbCWnglZvG1bzJ" //first upload id
                else -> "6gsCgbjcLFZqUz4NWofm" //fifth upload id
            }
            for (id in idList) {
                if (id.getString("gamification_object_id") == searchedId) {
                    return true
                }
            }
            return false
        }

        private suspend fun addObtainedGamificationPost(threshold: Int) {
            val toInsertId = when (threshold) {
                1 -> "D2tj9ImbCWnglZvG1bzJ" //first upload id
                else -> "6gsCgbjcLFZqUz4NWofm" //fifth upload id
            }
            val gamificationObject = hashMapOf(
                "gamification_object_id" to toInsertId
            )
            FirebaseFirestore.getInstance().collection("users")
                .document(CurrentUserSingleton.currentUser!!.id).collection("gamification_obtained")
                .add(gamificationObject)
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
                        userUpdate["password"] = encryptPassword(newPassword)
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
                FirebaseFirestore.getInstance().collection("posts")
                    .orderBy("date", Query.Direction.DESCENDING).get()
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
                addPostInList(post, noteList, isSaved)
            }
        }

        suspend fun addPostInList(postQuery: QueryDocumentSnapshot? = null, noteList: MutableList<Note>, isSaved: Boolean, postDocument: DocumentSnapshot? = null) {
            val post =
                postQuery ?: postDocument
            noteList.add(
                Note(
                    postId = post!!.id,
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

        suspend fun getPostFromId(noteId: String): DocumentSnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("posts").document(noteId).get()
                    .addOnSuccessListener { post ->
                        continuation.resume(post)
                    }
            }
        }

        private suspend fun getUserPosts(userId: String): QuerySnapshot {
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
            addPostInList(noteList = posts, isSaved = isSaved, postDocument = post)
        }

        suspend fun getUserSavedPosts(): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users")
                    .document(CurrentUserSingleton.currentUser!!.id)
                    .collection("saved_posts").orderBy("saved_date", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener { savedPosts ->
                        continuation.resume(savedPosts)
                    }
            }
        }

        suspend fun loadSavedPosts(
            noteList: MutableList<Note>,
            db: FirebaseFirestore,
            isEmpty: AtomicBoolean
        ) {
            val savedPosts = getUserSavedPosts()
            if (savedPosts.size() == 0) {
                isEmpty.set(true)
            }
            for (postId in savedPosts) {
                val post = postId.getString("post_id")?.let { getPostFromId(it) }
                if (post != null) {
                    addPostInList(noteList = noteList, isSaved = true, postDocument = post)
                }
            }
        }

        suspend fun loadUserPosts(
            noteList: MutableList<Note>,
            db: FirebaseFirestore,
            userId: String
        ) {
            //val allUserPosts = getUserPosts(userId)
            val allUserPosts = getAllPosts()
            for (post in allUserPosts) {
                if (post.getString("user_id") == userId) {
                    val isSaved = isPostSaved(post.id)
                    addPostInList(post, noteList, isSaved)
                }
            }
        }

        suspend fun searchPost(
            noteList: MutableList<Note>,
            db: FirebaseFirestore,
            key: String
        ) {
            val posts = getAllPosts()
            for (post in posts) {
                if (post.getString("title")?.contains(key)!! || post.getString("description")
                        ?.contains(key)!! || post.getString("category")?.contains(key)!!
                ) {
                    val isSaved = isPostSaved(post.id)
                    addPostInList(post, noteList, isSaved)
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
            Log.i("debFilter", "Sono apply")
            val iterator = postsToFilter.iterator()
            while (iterator.hasNext()) {
                val post = iterator.next()
                Log.i("debFilter", "Analizzo: ${post.date}")
                if (selectedCategory.isNotEmpty() && post.category != selectedCategory) {
                    iterator.remove()
                }
            }
        }

        private suspend fun checkDataUnique(username: String, email: String): Boolean {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").get()
                    .addOnSuccessListener { res ->
                        for (user in res) {
                            if (user.getString("username") == username || user.getString("email") == email) {
                                continuation.resume(false)
                            }
                        }
                        continuation.resume(true)
                    }
            }
        }

        suspend fun execSignup(
            name: String,
            surname: String,
            email: String,
            username: String,
            password: String,
            repeatPassword: String,
            db: FirebaseFirestore,
            internalDb: NoteForAllDatabase,
            ctx: Context,
            imageUri: Uri?,
            latitude: Double?,
            longitude: Double?
        ) {
            if (name.isNotEmpty() &&
                surname.isNotEmpty() &&
                email.isNotEmpty() &&
                username.isNotEmpty() &&
                password.isNotEmpty() &&
                repeatPassword.isNotEmpty() &&
                password == repeatPassword
            ) {
                if (checkDataUnique(username, email)) {
                    var userPicPos =
                        "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/users_pic%2Fdefault_user_pic.png?alt=media"
                    val user = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "email" to email,
                        "username" to username,
                        "password" to encryptPassword(password)
                    )
                    if (imageUri != null) {
                        userPicPos = uploadToStorage(imageUri, ctx, "users_pic", ".jpg")
                    }
                    user["user_pic"] = userPicPos
                    db.collection("users").add(user).addOnSuccessListener { user ->
                        Log.d("debSignup", "DocumentSnapshot added with ID: ${user.id}")
                        val currentUser = CurrentUser(
                            id = user.id
                        )
                        CurrentUserSingleton.currentUser = currentUser
                        CoroutineScope(Dispatchers.IO).launch {
                            if (longitude != null && latitude != null) {
                                val userTmp =
                                    User(userId = user.id, userLong = longitude, userLat = latitude)
                                internalDb.dao.insertUserId(userTmp)
                            } else {
                                val userTmp = User(userId = user.id, userLong = 0.0, userLat = 0.0)
                                internalDb.dao.insertUserId(userTmp)
                            }
                        }

                        val intent = Intent(ctx, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        ctx.startActivity(intent)
                    }
                        .addOnFailureListener { e ->
                            Log.w("debSignup", "Error adding document", e)
                        }
                } else {
                    //error username or email already exist
                }
            }
        }

        fun execLogin(
            key: String,
            password: String,
            db: FirebaseFirestore,
            internalDb: NoteForAllDatabase,
            ctx: Context
        ) {
            if (key.isNotEmpty() && password.isNotEmpty()) {
                db.collection("users").get().addOnSuccessListener { res ->
                    for (user in res) {
                        if ((user.getString("email") == key || user.getString("username") == key) &&
                            user.getString("password") == encryptPassword(password)
                        ) {
                            val currentUser = CurrentUser(
                                id = user.id
                            )
                            CurrentUserSingleton.currentUser = currentUser
                            CoroutineScope(Dispatchers.IO).launch {
                                val userCurr = User(userId = user.id, 0.0, 0.0)
                                internalDb.dao.insertUserId(userCurr)
                            }
                            val intent = Intent(ctx, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            ctx.startActivity(intent)
                        }
                    }
                }
            } else {
                //error key or password is wrong
            }
        }

        private suspend fun getUserBadgesId(userId: String): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .collection("gamification_obtained").get().addOnSuccessListener { idList ->
                        continuation.resume(idList)
                    }
            }
        }

        suspend fun loadUserBadges(
            userBadges: MutableList<Badge>,
            db: FirebaseFirestore,
            userId: String
        ) {
            val idList = getUserBadgesId(userId)
            for (idBadge in idList) {
                idBadge.getString("gamification_object_id")?.let {
                    db.collection("gamification_objects").document(it).get()
                        .addOnSuccessListener { badge ->
                            val imageRef = badge.getString("image_ref") ?: ""
                            val title = badge.getString("title") ?: ""
                            userBadges.add(
                                Badge(
                                    imageRef = imageRef,
                                    title = title
                                )
                            )
                        }
                }
            }
        }


        fun loadAllBadges(allDbBadges: MutableList<Badge>, db: FirebaseFirestore) {
            db.collection("gamification_objects").get().addOnSuccessListener { badges ->
                for (badge in badges) {
                    val imageRef = badge.getString("image_ref") ?: ""
                    val title = badge.getString("title") ?: ""
                    allDbBadges.add(
                        Badge(
                            imageRef = imageRef,
                            title = title
                        )
                    )
                }
            }
        }

        private fun encryptPassword(password: String): String {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(password.toByteArray())
            val hashedPasswordBytes = messageDigest.digest()
            return hashedPasswordBytes.joinToString("") { "%02x".format(it) }
        }
    }
}
