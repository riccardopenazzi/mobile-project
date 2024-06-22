package it.unibo.noteforall.ui.screen.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.firebase.StorageUtil.Companion.execSignup
import it.unibo.noteforall.ui.composables.LoadingAnimation
import it.unibo.noteforall.ui.composables.outlinedTextFieldColors
import it.unibo.noteforall.utils.LocationService
import it.unibo.noteforall.utils.PermissionStatus
import it.unibo.noteforall.utils.navigation.AuthenticationRoute
import it.unibo.noteforall.utils.rememberCameraLauncher
import it.unibo.noteforall.utils.rememberPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    db: FirebaseFirestore,
    navController: NavHostController,
    internalDb: NoteForAllDatabase
) {
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

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRepeatPasswordVisible by remember { mutableStateOf(false) }

    val locationService = koinInject<LocationService>()

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) { status ->
        Log.i("test", "When: " + locationService.coordinates.toString())
        when (status) {
            PermissionStatus.Granted -> {
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

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            locationService.requestCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    /* Bottom sheet */
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Photo picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedImageUri = it }
    )

    fun photoPicker() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    /* Camera */
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExplanation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    val cameraLauncher = rememberCameraLauncher()

    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(ctx, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var isSigninUp by remember {
        mutableStateOf(false)
    }

    fun takePicture() {
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            if (isSigninUp) {
                item { LoadingAnimation() }
            } else {
                item {
                    Text(
                        text = "NoteForAll",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 30.dp)
                    )
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Select profile image",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    /* Bottom sheet */
                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = ::photoPicker) {
                                    Text("Pick a photo")
                                }
                                Divider(Modifier.fillParentMaxWidth(0.8f))
                                TextButton(onClick = {
                                    takePicture()
                                    if (showExplanation) {
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "The camera permission is necessary to take a picture and use it as your profile icon. To enable the permission go to settings.",
                                                actionLabel = "Settings",
                                                duration = SnackbarDuration.Long
                                            )
                                            when (result) {
                                                SnackbarResult.ActionPerformed -> {
                                                    val intent = Intent(
                                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                        Uri.parse("package:${ctx.packageName}")
                                                    )
                                                    ctx.startActivity(intent)
                                                }

                                                SnackbarResult.Dismissed -> showExplanation = false
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

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        label = { Text(text = "Surname") },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email") },
                        colors = outlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(text = "Username") },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Password") },
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
                        },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = repeatPassword,
                        onValueChange = { repeatPassword = it },
                        label = { Text(text = "Repeat password") },
                        singleLine = true,
                        visualTransformation = if (isRepeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = {
                                isRepeatPasswordVisible = !isRepeatPasswordVisible
                            }) {
                                Icon(
                                    imageVector =
                                    if (isRepeatPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (isRepeatPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        colors = outlinedTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = ::requestLocation) {
                        Icon(Icons.Outlined.AddLocationAlt, "Add location icon")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Latitude: ${locationService.coordinates?.latitude ?: "-"}")
                    Text("Longitude: ${locationService.coordinates?.longitude ?: "-"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        isSigninUp = true
                        CoroutineScope(Dispatchers.Main).launch {
                            val res = execSignup(
                                name,
                                surname,
                                email,
                                username,
                                password,
                                repeatPassword,
                                db,
                                internalDb,
                                ctx,
                                selectedImageUri,
                                locationService.coordinates?.latitude,
                                locationService.coordinates?.longitude
                            )
                            if (!res) {
                                isSigninUp = false
                            }
                        }
                    }) {
                        Text(text = "Signup", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            navController.navigate(AuthenticationRoute.Login.route)
                        },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "Already have an account?",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

