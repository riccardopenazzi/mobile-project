package it.unibo.noteforall.ui.screen.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.TitleCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton

@Composable
fun LoginScreen(db: FirebaseFirestore) {
    var key by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight()
            ) {
                Text(
                    text = "Note For All",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(vertical = 50.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = key, onValueChange = { key = it }, label = {
                    Text(text = "Email or username")
                })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = {
                    Text(text = "Password")
                })
                Spacer(modifier = Modifier.height(14.dp))
                Button(onClick = { execLogin(key, password, db) }) {
                    Text(text = "Login")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = "Don't have an account?")
                }
            }
        }
    }
}

fun execLogin(key: String, password: String, db: FirebaseFirestore) {
    if (key.isNotEmpty() && password.isNotEmpty()) {
        db.collection("users").get().addOnSuccessListener { res ->
            for (user in res) {
                if ((user.getString("email") == key || user.getString("username") == key) &&
                    user.getString("password") == password
                ) {
                    Log.i("debLogin", "Login success test id = ${user.id}")
                    val currentUser = CurrentUser (
                        id = user.id,
                        key = key
                    )
                    CurrentUserSingleton.currentUser = currentUser
                    Log.i("debLogin", "current user info: ${CurrentUserSingleton.currentUser!!.id} ${CurrentUserSingleton.currentUser!!.key}")
                    return@addOnSuccessListener
                }
            }
            Log.i("debLogin", "Login failed, provided ${key} and ${password}")
        }.addOnFailureListener{
            exception -> Log.w("debLogin", "Error getting documents.", exception)
        }
    }
}