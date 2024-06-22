package it.unibo.noteforall.ui.screen.profile

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.ui.composables.BadgeExtended
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.ui.screen.myProfile.checkBadgeUnlocked
import it.unibo.noteforall.utils.Badge
import it.unibo.noteforall.utils.Note

@Composable
fun ProfileScreen(navController: NavHostController, userId: String, db: FirebaseFirestore) {

    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val userPicUrl = remember { mutableStateOf("") }
    var isLaunched by remember { mutableStateOf(false) }
    val posts = remember { mutableStateListOf<Note>() }
    val userBadges = remember { mutableStateListOf<Badge>() }
    val allDbBadges = remember { mutableStateListOf<Badge>() }

    db.collection("users").document(userId).get().addOnSuccessListener { user ->
        name.value = user.getString("name").toString()
        surname.value = user.getString("surname").toString()
        username.value = user.getString("username").toString()
        userPicUrl.value = user.getString("user_pic").toString()
    }.addOnFailureListener { exception ->
        Log.e("ProfileScreen", "Exception: ", exception)
    }

    LaunchedEffect(isLaunched) {
        if (!isLaunched) {
            StorageUtil.loadUserPosts(posts, userId)
            StorageUtil.loadUserBadges(userBadges, db, userId)
            StorageUtil.loadAllBadges(allDbBadges, db)
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
            LazyRow(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (badge in allDbBadges) {
                    val isBadgeUnlocked = checkBadgeUnlocked(badge.imageRef, userBadges)
                    item { BadgeExtended(title = badge.title, imageRef = badge.imageRef, instructions = badge.instructions,isUnlocked = isBadgeUnlocked) }
                }
            }
            val sortedPosts = posts.sortedBy { it.date }.reversed()
            for (post in sortedPosts) {
                NoteCard(navController = navController, note = post, db = db)
            }
        }
    }
}
