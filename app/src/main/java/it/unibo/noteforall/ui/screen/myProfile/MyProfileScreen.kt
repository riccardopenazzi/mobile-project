package it.unibo.noteforall.ui.screen.myProfile

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadAllBadges
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadUserBadges
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.loadUserPosts
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.utils.Badge
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.Note

@Composable
fun MyProfileScreen(navController: NavHostController, db: FirebaseFirestore) {
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val userPicUrl = remember { mutableStateOf("") }
    var isLaunched by remember { mutableStateOf(false) }
    val posts = remember { mutableStateListOf<Note>() }
    val userBadges = remember { mutableStateListOf<Badge>() }
    val allDbBadges = remember { mutableStateListOf<Badge>() }

    db.collection("users").document(CurrentUserSingleton.currentUser!!.id).get()
        .addOnSuccessListener { user ->
            name.value = user.getString("name").toString()
            surname.value = user.getString("surname").toString()
            username.value = user.getString("username").toString()
            userPicUrl.value = user.getString("user_pic").toString()
        }.addOnFailureListener { exception ->
            Log.i("debImg", "Errore durante il recupero dei dati dell'utente: ", exception)
        }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            loadUserPosts(posts, db, CurrentUserSingleton.currentUser!!.id)
            loadUserBadges(userBadges, db, CurrentUserSingleton.currentUser!!.id)
            loadAllBadges(allDbBadges, db)
            isLaunched = true
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
            .fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = userPicUrl.value,
                    contentDescription = "img",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = name.value + " " + surname.value,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                            .padding(6.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = username.value,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                            .padding(6.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            /*Text(
                text = "Badges here",
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                    .padding(30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )*/
            LazyRow() {
                for (badge in allDbBadges) {
                    val isBadgeUnlocked = checkBadgeUnlocked(badge.imageRef, userBadges)
                    item { AsyncImage(model = badge.imageRef, contentDescription = "Badge image", modifier = Modifier.size(40.dp).alpha(if (isBadgeUnlocked) 1f else 0.5f)) }
                }
            }
            for (post in posts) {
                NoteCard(navController = navController, note = post, db = db)
            }
        }
    }
}

fun checkBadgeUnlocked(searched: String, userBadges: MutableList<Badge>) : Boolean {
    for (badge in userBadges) {
        if (badge.imageRef == searched) {
            return true
        }
    }
    return false
}
