package it.unibo.noteforall.ui.screen.editProfile

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import it.unibo.noteforall.utils.CurrentUserSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class EditProfileState(
    val name: String = "",
    val surname: String = "",
    val username: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
    val repeatPassword: String = "",
    val imageURL: String = ""
)

interface EditProfileActions {
    fun setName(name: String)
    fun setSurname(surname: String)
    fun setUsername(username: String)
    fun setOldPassword(password: String)
    fun setNewPassword(password: String)
    fun setRepeatPassword(password: String)
    fun setImageURL(imageURL: String)
}

class EditProfileViewModel(db: FirebaseFirestore) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        db.collection("users").document(CurrentUserSingleton.currentUser?.id.toString()).get()
            .addOnSuccessListener { user ->
                actions.setName(user.getString("name").toString())
                actions.setSurname(user.getString("surname").toString())
                actions.setUsername(user.getString("username").toString())
                actions.setImageURL(user.getString("user_pic").toString())
            }
    }



    val actions = object : EditProfileActions {
        override fun setName(name: String) =
            _state.update { it.copy(name = name) }

        override fun setSurname(surname: String) =
            _state.update { it.copy(surname = surname) }

        override fun setUsername(username: String) =
            _state.update { it.copy(username = username) }

        override fun setOldPassword(password: String) =
            _state.update { it.copy(oldPassword = password) }

        override fun setNewPassword(password: String) =
            _state.update { it.copy(newPassword = password) }

        override fun setRepeatPassword(password: String) =
            _state.update { it.copy(repeatPassword = password) }

        override fun setImageURL(imageURL: String) =
            _state.update { it.copy(imageURL = imageURL) }
    }
    fun fecthData(db: FirebaseFirestore) {

    }
}

