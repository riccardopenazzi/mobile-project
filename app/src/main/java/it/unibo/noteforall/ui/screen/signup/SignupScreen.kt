package it.unibo.noteforall.ui.screen.signup

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.utils.CurrentUser
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.LocationService
import it.unibo.noteforall.utils.PermissionStatus
import it.unibo.noteforall.utils.rememberPermission
import org.koin.compose.koinInject

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

    val locationService = koinInject<LocationService>()

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        Log.i("test", "When: " + locationService.coordinates.toString())
        when (status) {
            PermissionStatus.Granted ->
            {
                locationService.requestCurrentLocation()
                Log.i("test", "Granted: " + locationService.coordinates.toString())
            }

            PermissionStatus.Denied -> {}
                // Gestire il caso di negazione dei permessi
                // actions.setShowLocationPermissionDeniedAlert(true)

            PermissionStatus.PermanentlyDenied -> {}
                // Gestire il caso di negazione dei permessi
                // actions.setShowLocationPermissionPermanentlyDeniedSnackbar(true)

            PermissionStatus.Unknown -> {
                Log.i("test", "Unknown: " + locationService.coordinates.toString())
            }
        }
    }
    //Log.d("test", locationPermission.status.isGranted.toString())

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            locationService.requestCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
        Log.d("test", locationService.coordinates.toString())
    }

    LaunchedEffect(locationService.isLocationEnabled) {
        //actions.setShowLocationDisabledAlert(locationService.isLocationEnabled == false)
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
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
            IconButton(onClick = ::requestLocation) {
                Icon(Icons.Outlined.AddLocationAlt, "Add location icon")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Latitude: ${locationService.coordinates?.latitude ?: "-"}")
            Text("Longitude: ${locationService.coordinates?.longitude ?: "-"}")
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
            val currentUser = CurrentUser(
                id = documentReference.id,
                key = username
            )
            CurrentUserSingleton.currentUser = currentUser
        }
            .addOnFailureListener { e ->
                Log.w("debSignup", "Error adding document", e)
            }
    }
}
