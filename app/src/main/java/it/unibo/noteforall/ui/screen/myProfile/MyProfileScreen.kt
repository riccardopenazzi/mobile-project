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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.ui.composables.NoteCard
import it.unibo.noteforall.ui.theme.Teal800
import it.unibo.noteforall.utils.CurrentUserSingleton

@Composable
fun MyProfileScreen(navController: NavHostController, db: FirebaseFirestore) {
    val userPicUrl = remember { mutableStateOf("") }
    db.collection("users").document(CurrentUserSingleton.currentUser!!.id).get().addOnSuccessListener { document ->
        val tmp = document.getString("user_pic").toString()
        userPicUrl.value = tmp
    }.addOnFailureListener {exception ->
        Log.i("debImg", "Errore durante il recupero dei dati dell'utente: ", exception)
    }
    Log.i("debImg", userPicUrl.value)
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
            .fillMaxSize()
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = userPicUrl.value,
                    contentDescription = "img",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(80.dp).clip(CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Name",
                        modifier = Modifier
                            .border(1.dp, Teal800, RoundedCornerShape(30))
                            .padding(6.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Surname",
                        modifier = Modifier
                            .border(1.dp, Teal800, RoundedCornerShape(30))
                            .padding(6.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Badges here",
                modifier = Modifier
                    .border(1.dp, Teal800, RoundedCornerShape(30))
                    .padding(30.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            PrintUserNotes(navController)
        }
    }
}

@Composable
fun PrintUserNotes(navController: NavHostController) {
    for (i in 0..9) {
        NoteCard(navController = navController)
    }
}
