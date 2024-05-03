package it.unibo.noteforall.ui.screen.newNote

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import it.unibo.noteforall.data.firebase.StorageUtil
import it.unibo.noteforall.utils.CurrentUserSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NewNoteState(
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val fileURI: Uri? = Uri.EMPTY,
    val imageURI: Uri? = Uri.EMPTY
)

interface NewNoteActions {
    fun setTitle(title: String)
    fun setCategory(category: String)
    fun setDescription(description: String)
    fun setFileURI(fileURI: Uri?)
    fun setImageURI(imageURI: Uri?)
    fun uploadPost(
        ctx: Context,
        imageUri: Uri?,
        title: String,
        description: String,
        category: String,
        noteUri: Uri?,
        navController: NavHostController
    )
}

class NewNoteViewModel : ViewModel() {
    private var _state = MutableStateFlow(NewNoteState())
    val state = _state.asStateFlow()

    val actions = object : NewNoteActions {
        override fun setTitle(title: String) =
            _state.update { it.copy(title = title) }

        override fun setCategory(category: String) =
            _state.update { it.copy(category = category) }

        override fun setDescription(description: String) =
            _state.update { it.copy(description = description) }

        override fun setFileURI(fileURI: Uri?) =
            _state.update { it.copy(fileURI = fileURI ?: Uri.EMPTY) }

        override fun setImageURI(imageURI: Uri?) =
            _state.update { it.copy(imageURI = imageURI ?: Uri.EMPTY) }

        override fun uploadPost(
            ctx: Context,
            imageUri: Uri?,
            title: String,
            description: String,
            category: String,
            noteUri: Uri?,
            navController: NavHostController
        ) {
            val post = hashMapOf(
                "title" to title,
                "category" to category,
                "description" to description,
                "user_id" to CurrentUserSingleton.currentUser!!.id
            )
            imageUri?.let {
                noteUri?.let {
                    StorageUtil.createPost(
                        imageUri = imageUri, context = ctx, post = post, noteUri = noteUri, navController = navController
                    )
                }
            }
        }
    }
}