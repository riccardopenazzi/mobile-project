package it.unibo.noteforall.ui.screen.editProfile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import it.unibo.noteforall.ui.composables.LoadingAnimation
import it.unibo.noteforall.ui.composables.MyAlertDialog
import it.unibo.noteforall.ui.composables.outlinedTextFieldColors
import it.unibo.noteforall.utils.rememberCameraLauncher
import it.unibo.noteforall.utils.rememberPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: EditProfileState,
    actions: EditProfileActions,
    navController: NavHostController
) {
    var isChangingInfo by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var isOldPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isRepeatPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Bottom sheet
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Photo picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isPhotoSelected by remember { mutableStateOf(false) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedImageUri = it }
    )

    fun photoPicker() {
        isPhotoSelected = true
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberCameraLauncher()

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        isPhotoSelected = true
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            if (ContextCompat.checkSelfPermission(ctx, cameraPermission.permission) == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ctx as Activity, cameraPermission.permission)) {
                    showExplanation = true
                    showBottomSheet = false
                } else {
                    cameraPermission.launchPermissionRequest()
                }
            }
        }
    }

    if (showDialog) {
        /* Pressed save button */
        if (isChangingInfo) {
            MyAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    showDialog = false
                    editProfileInfo(ctx, state, actions, navController, selectedImageUri)
                    showLoading = true
                },
                title = "Confirm changes?",
                text = "Confirming changes will edit your profile and bring you to your profile page",
                icon = null
            )
        } else {
            /* Pressed cancel button */
            MyAlertDialog(
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    showDialog = false
                    navController.popBackStack()
                },
                title = "Delete changes?",
                text = "Deleting changes will also bring you to your profile page",
                icon = null
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .padding(start = 10.dp, end = 10.dp)
        ) {//min padding 56
            if (!isChangingInfo) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = { showBottomSheet = true }, Modifier.size(80.dp)) {
                        AsyncImage(
                            model = if (isPhotoSelected && selectedImageUri != null) selectedImageUri else state.imageURL,
                            contentDescription = "img",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = actions::setName,
                        label = {
                            Text(text = "Name")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.surname,
                        onValueChange = actions::setSurname,
                        label = {
                            Text(text = "Surname")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = actions::setUsername,
                        label = {
                            Text(text = "Username")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.oldPassword,
                        onValueChange = actions::setOldPassword,
                        label = {
                            Text(text = "Old Password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isOldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isOldPasswordVisible = !isOldPasswordVisible }) {
                                Icon(
                                    imageVector =
                                        if (isOldPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (isOldPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.newPassword,
                        onValueChange = actions::setNewPassword,
                        label = {
                            Text(text = "New Password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                                Icon(
                                    imageVector =
                                        if (isNewPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (isNewPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.repeatPassword,
                        onValueChange = actions::setRepeatPassword,
                        label = {
                            Text(text = "Repeat password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isRepeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(
                                onClick = { isRepeatPasswordVisible = !isRepeatPasswordVisible}
                            ) {
                                Icon(
                                    imageVector =
                                        if (isRepeatPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (isRepeatPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 60.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                isChangingInfo = true
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
            if (showLoading) {
                item { LoadingAnimation() }
            }
        }
    }

    /* Bottom sheet */
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = ::photoPicker) {
                    Text("Pick a photo")
                }
                Divider(Modifier.fillMaxWidth(0.8f))
                TextButton(onClick = {
                    takePicture()
                    if (showExplanation) {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "The camera permission is required.",
                                actionLabel = "Go to settings",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                showExplanation = false
                                ctx.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", ctx.packageName, null)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                )
                            }
                        }
                    }
                }) {
                    Text("Take a picture")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

fun editProfileInfo(
    ctx: Context,
    state: EditProfileState,
    actions: EditProfileActions,
    navController: NavHostController,
    selectedImageUri: Uri?
) {
    CoroutineScope(Dispatchers.Main).launch {
        actions.changeUserInfo(
            selectedImageUri,
            ctx,
            state.name,
            state.surname,
            state.username.trim(),
            state.oldPassword,
            state.newPassword,
            state.repeatPassword,
            navController
        )
    }
}