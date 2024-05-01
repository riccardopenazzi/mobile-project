package it.unibo.noteforall.ui.composables

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.ui.theme.Teal800
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute

@Composable
fun NoteCardExtended(
    navController: NavHostController,
    noteId: String,
    db: FirebaseFirestore
) {
    var isSaved by remember { mutableStateOf(note.isSaved) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Column (
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            /*Author*/
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "", modifier = Modifier.size(40.dp))
                AsyncImage(
                    model = note.authorPicRef,
                    contentDescription = "Author pic",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                note.author?.let { Text(text = it, modifier = Modifier.weight(1.5f)) }
                IconButton(onClick = {
                    if (!isSaved) {
                        note.postId?.let { savePost(it, db) }
                    } else {
                        note.postId?.let { unsavePost(it, db) }
                    }
                    isSaved = !isSaved
                }) {
                    Icon(
                        imageVector = if (!isSaved) Icons.Outlined.StarBorder else Icons.Outlined.Star,
                        contentDescription = ""
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            //(imageVector = Icons.Rounded.Image, contentDescription = "Note preview", modifier = Modifier.size(100.dp))
            AsyncImage(
                model = note.picRef,
                contentDescription = "Note preview",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(400.dp).clip(RoundedCornerShape(10))
            )
            Spacer(modifier = Modifier.size(10.dp))
            note.title?.let { Text(text = it, style = MaterialTheme.typography.titleLarge) }
            Spacer(modifier = Modifier.size(10.dp))
            note.category?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .border(1.dp, Teal800, RoundedCornerShape(30))
                        .padding(6.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            note.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Download, "Download icon")
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Download note",
                    modifier = Modifier
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(30))
                        .padding(6.dp)
                )
            }
        }
    }
}

fun savePost(postId: String, db: FirebaseFirestore) {
    val savedPost = hashMapOf(
        "post_id" to postId
    )
    db.collection("users").document(CurrentUserSingleton.currentUser!!.id).collection("saved_posts")
        .add(savedPost)
}

fun unsavePost(postId: String, db: FirebaseFirestore) {
    val savedPost = hashMapOf(
        "post_id" to postId
    )
    db.collection("users").document(CurrentUserSingleton.currentUser!!.id).collection("saved_posts")
        .whereEqualTo("post_id", postId).get().addOnSuccessListener { post ->
            if (!post.isEmpty) {
                Log.i("deb", "Post non è empty")
                post.documents.first().reference.delete()
            }
        }
}