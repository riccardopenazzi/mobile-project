package it.unibo.noteforall.ui.composables

import android.text.format.DateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.savePost
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.unsavePost
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    navController: NavHostController,
    note: Note,
    db: FirebaseFirestore
) {

    var isSaved by remember { mutableStateOf(note.isSaved) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            NoteForAllRoute.routes.find {
                it.route == backStackEntry?.destination?.route
            } ?: NoteForAllRoute.Home
        }
    }
    Card(
        onClick = { navController.navigate(NoteForAllRoute.ViewNote.buildRoute(note.postId!!, currentRoute == NoteForAllRoute.MyProfile)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.padding(10.dp),
        shape = RoundedCornerShape(5),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column (
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Author*/
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = note.authorPicRef,
                    contentDescription = "Author pic",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                note.author?.let { ClickableText(
                    text = AnnotatedString(it),
                    style = TextStyle(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.weight(1.5f),
                    onClick = { navController.navigate(NoteForAllRoute.Profile.buildRoute(note.userId)) }
                )}
                if (note.userId != CurrentUserSingleton.currentUser!!.id) {
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = {
                        if (!isSaved) {
                            note.isSaved = true
                            CoroutineScope(Dispatchers.Main).launch {
                                note.postId?.let { savePost(it, db) }
                            }
                        } else {
                            note.isSaved = false
                            note.postId?.let { unsavePost(it, db) }
                        }
                        isSaved = !isSaved
                    }) {
                        Icon(
                            imageVector = if (!isSaved) Icons.Outlined.StarBorder else Icons.Outlined.Star,
                            contentDescription = if (isSaved) "Unsave the post" else "Save the post"
                        )
                    }
                } else if (currentRoute == NoteForAllRoute.MyProfile) {
                    Text(text = note.numSaved.toString())
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = "Bookmark icon"
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            AsyncImage(
                model = note.picRef,
                contentDescription = "Note preview",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(400.dp)
                    .clip(RoundedCornerShape(5))
            )
            Spacer(modifier = Modifier.size(10.dp))
            note.date?.let {
                Text(
                    text = DateFormat.format("dd/MM/yyyy", it.toDate()).toString(),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            note.title?.let {
                Text(text = it,style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.size(20.dp))
            note.category?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                        .padding(6.dp)
                )
            }
        }
    }
}
