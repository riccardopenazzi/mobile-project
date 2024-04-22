package it.unibo.noteforall.ui.screen.signup

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(db: FirebaseFirestore) {
    var name by remember {
        mutableStateOf("")
    }
    var surname by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var repeatPassword by remember {
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
                    modifier = Modifier.padding(vertical = 30.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Select profile image",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { /* TODO */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Name") })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text(text = "Surname") })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(text = "Username") })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") })
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    label = { Text(text = "Repeat password") })
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    execSignup(
                        name,
                        surname,
                        email,
                        username,
                        password,
                        repeatPassword,
                        db
                    )
                }) {
                    Text(text = "Signup")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = "Already have an account?")
                }
            }
        }
    }
}

fun execSignup(
    name: String,
    surname: String,
    email: String,
    username: String,
    password: String,
    repeatPassword: String,
    db: FirebaseFirestore
) {
    if (name.isNotEmpty() &&
        surname.isNotEmpty() &&
        email.isNotEmpty() &&
        username.isNotEmpty() &&
        password.isNotEmpty() &&
        repeatPassword.isNotEmpty() &&
        password == repeatPassword
    ) {
        val user = hashMapOf(
            "name" to name,
            "surname" to surname,
            "email" to email,
            "username" to username,
            "password" to password
        )
        db.collection("users").add(user).addOnSuccessListener { documentReference ->
            Log.d("debSignup", "DocumentSnapshot added with ID: ${documentReference.id}")
        }
            .addOnFailureListener { e ->
                Log.w("debSignup", "Error adding document", e)
            }
    }
}
