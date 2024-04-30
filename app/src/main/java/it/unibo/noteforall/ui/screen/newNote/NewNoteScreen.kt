package it.unibo.noteforall.ui.screen.newNote

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.unibo.noteforall.ui.theme.Teal800
import it.unibo.noteforall.data.firebase.StorageUtil

@Composable
fun NewNoteScreen() {

    var title by remember {
        mutableStateOf("")
    }
    var category by remember {
        mutableStateOf("")
    }
    var description by remember {
        mutableStateOf("")
    }

    /* Photo picker */
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

    /* File picker */
    var noteUri by remember { mutableStateOf<Uri?>(null) }
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            noteUri = it
        })

    val ctx = LocalContext.current

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {//min padding 56
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = {
                Text(text = "Title")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = {
                Text(text = "Category")
            }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = {
                Text(text = "Description")
            }, modifier = Modifier.fillMaxWidth(), minLines = 10)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { documentPickerLauncher.launch(arrayOf("application/pdf")) }) {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = "Choose note"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Upload note",
                    modifier = Modifier
                        .border(1.dp, Teal800, RoundedCornerShape(30))
                        .padding(6.dp)
                        .width(180.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = ::photoPicker) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = "Choose note preview"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Choose note preview",
                    modifier = Modifier
                        .border(1.dp, Teal800, RoundedCornerShape(30))
                        .padding(6.dp)
                        .width(180.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        uploadPost(selectedImageUri, ctx, title, description, category, noteUri)
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text(text = "Save", color = Color.White)
                }
            }
        }
    }
}

fun uploadPost(
    imageUri: Uri?,
    ctx: Context,
    title: String,
    description: String,
    category: String,
    noteUri: Uri?
) {
    val post = hashMapOf(
        "title" to title,
        "category" to category,
        "description" to description
    )
    imageUri?.let {
        val tmp = it
        noteUri?.let {
            StorageUtil.uploadToStorage(imageUri = tmp, context = ctx, type = "image", "post_pic", post, it)
        }
    }
}