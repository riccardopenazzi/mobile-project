package it.unibo.noteforall.ui.composables

import android.app.SearchManager
import android.content.Intent
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.downloadNote
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadNote
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.savePost
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.unsavePost
import it.unibo.noteforall.utils.Note
import it.unibo.noteforall.utils.navigation.NoteForAllRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NoteCardExtended(
    navController: NavHostController,
    noteId: String,
    db: FirebaseFirestore
) {
    var isLaunched by remember { mutableStateOf(false) }
    val posts = remember { mutableStateListOf<Note>() }
    val ctx = LocalContext.current

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            loadNote(noteId, db, posts)
            isLaunched = true
        }
    }

    for (note in posts) {
        var isSaved by remember { mutableStateOf(note.isSaved) }
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(5),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                /*Author*/
                Row(
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
                    note.author?.let {
                        ClickableText(
                            text = AnnotatedString(it),
                            style = TextStyle(MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.weight(1.5f),
                            onClick = {
                                navController.navigate(
                                    NoteForAllRoute.Profile.buildRoute(
                                        note.userId
                                    )
                                )
                            }
                        )
                    }
                    IconButton(onClick = {
                        if (!isSaved) {
                            CoroutineScope(Dispatchers.Main).launch {
                                note.postId?.let { savePost(it, db) }
                            }
                        } else {
                            note.postId?.let { unsavePost(it, db) }
                        }
                        isSaved = !isSaved
                    }) {
                        Icon(
                            imageVector = if (!isSaved) Icons.Outlined.StarBorder else Icons.Outlined.Star,
                            contentDescription = if (isSaved) "Unsave the post" else "Save the post"
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
                note.title?.let {
                    Text(text = it, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Row (
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    note.category?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(30)
                                )
                                .padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                            putExtra(SearchManager.QUERY, note.title)
                        }
                        if (intent.resolveActivity(ctx.packageManager) != null) {
                            ctx.startActivity(intent)
                        }
                    }) {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "search icon")
                        Text(text = "Search more")
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                note.description?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = {
                        note.noteRef?.let { downloadNote(it, ctx) }
                    }) {
                        Icon(Icons.Outlined.Download, "Download icon")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Download note",
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                            .padding(6.dp)
                    )
                }
            }
        }
    }
}
