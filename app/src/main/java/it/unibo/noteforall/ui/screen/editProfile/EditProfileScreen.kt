package it.unibo.noteforall.ui.screen.editProfile

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.utils.CurrentUserSingleton
import it.unibo.noteforall.utils.rememberCameraLauncher
import it.unibo.noteforall.utils.rememberPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(db: FirebaseFirestore) {
    var name by remember {
        mutableStateOf("")
    }
    var surname by remember {
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
    db.collection("users").document(CurrentUserSingleton.currentUser?.id.toString()).get()
        .addOnSuccessListener { user ->
            name = user.getString("name").toString()
            surname = user.getString("surname").toString()
            username = user.getString("username").toString()
            password = user.getString("password").toString()
            repeatPassword = password
        }

    // Bottom sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val ctx = LocalContext.current

    val cameraLauncher = rememberCameraLauncher()

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {//min padding 56
        item {
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = {
                //::takePicture
                showBottomSheet = true
            }) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    "Profile icon",
                    Modifier.size(80.dp))
            }

            if (showBottomSheet) {
                ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
                    Column {
                        TextButton(onClick = {

                        }) {
                            Text("Pick a photo")
                        }
                        TextButton(onClick = ::takePicture) {
                            Text("Take a picture")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = {
                Text(text = "Name")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = surname, onValueChange = { surname = it }, label = {
                Text(text = "Surname")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = username, onValueChange = { username = it }, label = {
                Text(text = "Username")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = {
                Text(text = "Password")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = repeatPassword,
                onValueChange = { repeatPassword = it },
                label = {
                    Text(text = "Repeat password")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Save", color = Color.White)
                }
            }
        }
    }
}
@Composable
fun photoPicker() {
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

fun getUserInfo(db: FirebaseFirestore) {

}