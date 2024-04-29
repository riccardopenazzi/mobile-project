package it.unibo.noteforall

import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.data.NoteForAllDatabase
import it.unibo.noteforall.ui.screen.editProfile.EditProfileViewModel
import it.unibo.noteforall.utils.LocationService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { LocationService(get()) }

    val db = Firebase.firestore

    single { Room.databaseBuilder(
        get(),
        NoteForAllDatabase::class.java,
        "noteforall.db"
    ).build() }

    viewModel { EditProfileViewModel(db) }
}