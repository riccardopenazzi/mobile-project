package it.unibo.noteforall.data.firebase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
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
import it.unibo.noteforall.utils.Notification
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
        private const val DEFAULT_USER_PIC_URL =
            "https://firebasestorage.googleapis.com/v0/b/noteforall-2f581.appspot.com/o/users_pic%2Fdefault_user_pic.png?alt=media"
        private const val FIRST_UPLOAD_GAMIFICATION_ID = "D2tj9ImbCWnglZvG1bzJ"
        private const val FIFTH_UPLOAD_GAMIFICATION_ID = "6gsCgbjcLFZqUz4NWofm"
        private const val POSTS_NOTE_BUCKET = "posts_note"
        private const val POSTS_PIC_BUCKET = "posts_pic"
        private const val USERS_PIC_BUCKET = "users_pic"

        suspend fun createPost(
            imageUri: Uri,
            noteUri: Uri,
            context: Context,
            post: HashMap<String, String>,
            navController: NavHostController
        ) {
            post["pic_ref"] = uploadToStorage(imageUri, context, POSTS_PIC_BUCKET, "jpg")
            post["note_ref"] = uploadToStorage(noteUri, context, POSTS_NOTE_BUCKET, "pdf")
            if (uploadPost(post)) {
                //gamification
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
                Toast.makeText(
                    context,
                    "Something went wrong while uploading posts, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private suspend fun uploadPost(post: HashMap<String, String>): Boolean {
            return suspendCoroutine { continuation ->
                val postRef = FirebaseFirestore.getInstance().collection("posts")
                postRef.add(post).addOnSuccessListener { newPost ->
                    val addDate = hashMapOf(
                        "date" to Timestamp.now()
                    )
                    val numSaved = hashMapOf(
                        "num_saved" to 0
                    )
                    postRef.document(newPost.id).update(addDate as Map<String, Any>)
                        .addOnSuccessListener {
                            postRef.document(newPost.id).update(numSaved as Map<String, Number>)
                                .addOnSuccessListener {
                                    continuation.resume(true)
                                }
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
                1 -> FIRST_UPLOAD_GAMIFICATION_ID
                else -> FIFTH_UPLOAD_GAMIFICATION_ID
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
                1 -> FIRST_UPLOAD_GAMIFICATION_ID
                else -> FIFTH_UPLOAD_GAMIFICATION_ID
            }
            val gamificationObject = hashMapOf(
                "gamification_object_id" to toInsertId
            )
            FirebaseFirestore.getInstance().collection("users")
                .document(CurrentUserSingleton.currentUser!!.id).collection("gamification_obtained")
                .add(gamificationObject)
            createNotification("", "", toInsertId, "gamification")
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
                            Toast.makeText(
                                context,
                                "Error while uploading documents to db, please try again",
                                Toast.LENGTH_SHORT
                            ).show()
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
                val picPosition = uploadToStorage(imageUri, context, USERS_PIC_BUCKET, "jpg")
                userUpdate["user_pic"] = picPosition
            }
            //change password if necessary
            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && repeatNewPassword.isNotEmpty()) {
                if (newPassword == repeatNewPassword) {
                    if (checkPassword(oldPassword)) {
                        userUpdate["password"] = encryptPassword(newPassword)
                    } else {
                        //error old password doesn't correspond
                        Toast.makeText(
                            context,
                            "Old password is not correct",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    //error new passwords don't match
                    Toast.makeText(
                        context,
                        "New passwords don't match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                    continuation.resume(!res.isEmpty)
                }.addOnFailureListener { exception ->
                    Log.e("isPostSaved", "Exception: $exception")
                }
            }
        }

        suspend fun savePost(postId: String, db: FirebaseFirestore) {
            val time = Timestamp.now()
            val savedPost = hashMapOf(
                "post_id" to postId,
                "saved_date" to time
            )
            db.collection("users").document(CurrentUserSingleton.currentUser!!.id)
                .collection("saved_posts")
                .add(savedPost)
            db.collection("posts").document(postId).update("num_saved", FieldValue.increment(1))
            val targetId = getAuthorFromPostId(postId)
            createNotification(targetId, postId, "","save")
        }

        fun unsavePost(postId: String, db: FirebaseFirestore) {
            db.collection("users").document(CurrentUserSingleton.currentUser!!.id)
                .collection("saved_posts")
                .whereEqualTo("post_id", postId).get().addOnSuccessListener { post ->
                    if (!post.isEmpty) {
                        Log.i("debSave", "Sono qui")
                        post.documents.first().reference.delete()
                        db.collection("posts").document(postId)
                            .update("num_saved", FieldValue.increment(-1))
                        deleteNotification(postId)
                    }
                }
        }

         private fun deleteNotification(postId: String) {
            FirebaseFirestore.getInstance().collection("notifications").whereEqualTo("post_target", postId).get().addOnSuccessListener { res ->
                for (notification in res) {
                    if (notification.getString("id_source") != null && notification.getString("id_source") == CurrentUserSingleton.currentUser!!.id) {
                        FirebaseFirestore.getInstance().collection("notifications").document(notification.id).delete()
                    }
                }
            }
        }

        private suspend fun getAllPosts(): QuerySnapshot {
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
            isLoadFinished: AtomicBoolean? = null
        ) {
            var removed = 0
            val allPosts = getAllPosts()
            for (post in allPosts) {
                if (post.getString("user_id") != CurrentUserSingleton.currentUser!!.id) {
                    val isSaved = isPostSaved(post.id)
                    addPostInList(post, noteList, isSaved)
                } else {
                    removed++
                }
                if (allPosts.size() == (noteList.size + removed)) {
                    isLoadFinished?.set(true)
                }
            }
        }

        private suspend fun addPostInList(
            postQuery: QueryDocumentSnapshot? = null,
            noteList: MutableList<Note>,
            isSaved: Boolean,
            postDocument: DocumentSnapshot? = null
        ) {
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
                    date = post.getTimestamp("date")!!,
                    numSaved = post.getLong("num_saved"),
                )
            )
        }

        private suspend fun getPostFromId(noteId: String): DocumentSnapshot {
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
            posts: MutableList<Note>
        ) {
            val post = getPostFromId(noteId)
            val isSaved = isPostSaved(noteId)
            addPostInList(noteList = posts, isSaved = isSaved, postDocument = post)
        }

        private suspend fun getUserSavedPosts(): QuerySnapshot {
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
            userId: String
        ) {
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
            selectedDateMillis: Timestamp? = null,
        ) {
            var iterator = postsToFilter.iterator()
            if (selectedCategory != "") {
                while (iterator.hasNext()) {
                    val post = iterator.next()
                    if (selectedCategory.isNotEmpty() && post.category != selectedCategory) {
                        iterator.remove()
                    }
                }
            }
            if (selectedDateMillis != null) {
                iterator = postsToFilter.iterator()
                while (iterator.hasNext()) {
                    val post = iterator.next()
                    if (post.date!! < selectedDateMillis) {
                        iterator.remove()
                    }
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

        private fun checkEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
            return email.matches(emailRegex.toRegex())
        }

        private suspend fun insertUser(user: HashMap<String, String>): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").add(user)
                    .addOnSuccessListener { success ->
                        continuation.resume(success.id)
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
            internalDb: NoteForAllDatabase,
            ctx: Context,
            imageUri: Uri?,
            latitude: Double?,
            longitude: Double?
        ): Boolean {
            if (name.isNotEmpty() &&
                surname.isNotEmpty() &&
                username.isNotEmpty() &&
                password.isNotEmpty() &&
                repeatPassword.isNotEmpty()
            ) {
                if (password == repeatPassword) {
                    if (checkEmail(email)) {
                        if (checkDataUnique(username, email)) {
                            var userPicPos =
                                DEFAULT_USER_PIC_URL
                            val user = hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "email" to email,
                                "username" to username,
                                "password" to encryptPassword(password)
                            )
                            if (imageUri != null) {
                                userPicPos =
                                    uploadToStorage(imageUri, ctx, USERS_PIC_BUCKET, ".jpg")
                            }
                            user["user_pic"] = userPicPos
                            val insertId = insertUser(user)
                            if (insertId != "") {
                                val currentUser = CurrentUser(
                                    id = insertId
                                )
                                CurrentUserSingleton.currentUser = currentUser
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (longitude != null && latitude != null) {
                                        val userTmp =
                                            User(
                                                userId = insertId,
                                                userLong = longitude,
                                                userLat = latitude
                                            )
                                        internalDb.dao.insertUserId(userTmp)
                                    } else {
                                        val userTmp =
                                            User(userId = insertId, userLong = 0.0, userLat = 0.0)
                                        internalDb.dao.insertUserId(userTmp)
                                    }
                                }
                                val intent = Intent(ctx, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                ctx.startActivity(intent)
                                return true
                            } else {
                                return false
                            }
                        } else {
                            //error username or email already exist
                            Toast.makeText(
                                ctx,
                                "Error username or email already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                            return false
                        }
                    } else {
                        //error email doesn't match regex
                        Toast.makeText(
                            ctx,
                            "Error invalid email, please check it's correct and try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        return false
                    }
                } else {
                    Toast.makeText(
                        ctx,
                        "Error password and repeat password don't match, please correct them and try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            } else {
                //error some fields are empty
                Toast.makeText(
                    ctx,
                    "Error some fields are empty, please fill every blank and try again",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        suspend fun execLogin(
            key: String,
            password: String,
            internalDb: NoteForAllDatabase,
            ctx: Context
        ): Boolean {
            if (key.isNotEmpty() && password.isNotEmpty()) {
                val users = getAllUsers()
                for (user in users) {
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
                        return true
                    }
                }
                //error no user found in db
                Toast.makeText(
                    ctx,
                    "Error username or password wrong",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
                //error key or password empty
                Toast.makeText(
                    ctx,
                    "Error username or password is empty",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        private suspend fun getAllUsers(): QuerySnapshot {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").get()
                    .addOnSuccessListener { users ->
                        continuation.resume(users)
                    }
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
                            val instructions = badge.getString("instructions") ?: ""
                            userBadges.add(
                                Badge(
                                    imageRef = imageRef,
                                    title = title,
                                    instructions = instructions
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
                    val instructions = badge.getString("instructions") ?: ""
                    allDbBadges.add(
                        Badge(
                            imageRef = imageRef,
                            title = title,
                            instructions = instructions
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

        private suspend fun getUsernameFromId(id: String): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").document(id).get()
                    .addOnSuccessListener { doc ->
                        continuation.resume(doc.getString("username").toString())
                    }
            }
        }

        private suspend fun getAuthorFromPostId(idPost: String): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("posts").document(idPost).get()
                    .addOnSuccessListener { post ->
                        continuation.resume(post.getString("user_id").toString())
                    }
            }
        }

        private suspend fun getGamificationImage(idGamificationObject: String): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("gamification_objects")
                    .document(idGamificationObject).get().addOnSuccessListener { res ->
                        continuation.resume(res.getString("image_ref").toString())
                    }
            }
        }

        private suspend fun createNotification(
            idTarget: String,
            postTarget: String,
            gamificationTarget: String,
            type: String
        ) {
            if (idTarget != CurrentUserSingleton.currentUser!!.id) {
                val username = getUsernameFromId(CurrentUserSingleton.currentUser!!.id)
                val sourcePicRef =
                    if (type == "save") getUserPicFromId(CurrentUserSingleton.currentUser!!.id) else
                        getGamificationImage(gamificationTarget)
                val content = when (type) {
                    "save" -> "$username saved your post"
                    else -> "New badge obtained"
                }
                val notification = hashMapOf(
                    "id_target" to if (type == "save") idTarget else CurrentUserSingleton.currentUser!!.id,
                    "id_source" to if (type == "save") CurrentUserSingleton.currentUser!!.id else "",
                    "content" to content,
                    "post_target" to postTarget,
                    "source_pic_ref" to sourcePicRef,
                    "is_read" to false,
                    "date" to Timestamp.now()
                )
                FirebaseFirestore.getInstance().collection("notifications").add(notification)
            }
        }

        suspend fun getUserPicFromId(id: String): String {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").document(id).get()
                    .addOnSuccessListener { res ->
                        val userPic = res.getString("user_pic") ?: DEFAULT_USER_PIC_URL
                        continuation.resume(userPic)
                    }
            }
        }

        private suspend fun getAllUserNotifications(id: String): List<DocumentSnapshot> {
            return suspendCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("notifications")
                    .orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener { res ->
                        val filteredRes =
                            res.documents.filter { current -> current.getString("id_target") == id }
                        continuation.resume(filteredRes)
                    }
            }
        }

        suspend fun createNotificationList(
            id: String,
            notificationList: MutableList<Notification>
        ) {
            val userNotification = getAllUserNotifications(id)
            for (notification in userNotification) {
                notificationList.add(
                    Notification(
                        content = notification.getString("content")!!,
                        idSource = notification.getString("id_source")!!,
                        idTarget = notification.getString("id_target")!!,
                        postTarget = notification.getString("post_target")!!,
                        sourcePicRef = notification.getString("source_pic_ref")!!,
                        isRead = notification.getBoolean("is_read")!!,
                        date = notification.getDate("date")!!
                    )
                )
            }

        }

        suspend fun checkExistNewNotification(id: String): Boolean {
            val userNotification = getAllUserNotifications(id)
            for (notification in userNotification) {
                if (notification.getBoolean("is_read") == false) {
                    return true
                }
            }
            return false
        }

        suspend fun readAllNotifications(id: String) {
            val userNotification = getAllUserNotifications(id)
            for (notification in userNotification) {
                FirebaseFirestore.getInstance().collection("notifications")
                    .document(notification.id).update("is_read", true)
            }
        }

    }
}
