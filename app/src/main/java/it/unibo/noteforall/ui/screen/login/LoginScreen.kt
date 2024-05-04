package it.unibo.noteforall.ui.screen.login

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.MainActivity
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.database.User
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.navigation.AuthenticationRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    db: FirebaseFirestore,
    navController: NavHostController,
    internalDb: NoteForAllDatabase
) {
    val ctx = LocalContext.current

    var key by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    var isPasswordVisible by remember { mutableStateOf(false) }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "NoteForAll",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(vertical = 50.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = key, onValueChange = { key = it }, label = {
                Text(text = "Email or username")
            })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = "Password")
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector =
                                if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    execLogin(key, password, db) { success, id ->
                        if (success) {
                            Log.i("debLogin", "login ok")
                            if (id != null) {
                                val currentUser = CurrentUser(
                                    id = id
                                )
                                CurrentUserSingleton.currentUser = currentUser
                                CoroutineScope(Dispatchers.IO).launch {
                                    val user = User(userId = id)
                                    internalDb.dao.insertUserId(user)
                                }
                            }

                            val intent = Intent(ctx, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            ctx.startActivity(intent)
                        } else {
                            Log.i("debLogin", "login NON ok")
                        }
                    }
                }
            ) {
                Text(text = "Login", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    navController.navigate(AuthenticationRoute.Signup.route)
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Don't have an account?", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun execLogin(
    key: String,
    password: String,
    db: FirebaseFirestore,
    onResult: (Boolean, String?) -> Unit
) {
    if (key.isNotEmpty() && password.isNotEmpty()) {
        db.collection("users").get().addOnSuccessListener { res ->
            for (user in res) {
                if ((user.getString("email") == key || user.getString("username") == key) &&
                    user.getString("password") == password
                ) {
                    Log.i("debLogin", "Login success test id = ${user.id}")
                    onResult(
                        true,
                        user.id
                    ) // Chiamare la funzione di callback con true se il login ha successo
                    return@addOnSuccessListener
                }
            }
            // Se il ciclo termina senza trovare corrispondenze, chiamare la funzione di callback con false
            onResult(false, null)
        }.addOnFailureListener { exception ->
            Log.w("debLogin", "Error getting documents.", exception)
            // Se si verifica un errore durante la richiesta al database, chiamare la funzione di callback con false
            onResult(false, null)
        }
    } else {
        // Se uno dei campi è vuoto, chiamare la funzione di callback con false
        onResult(false, null)
    }
}