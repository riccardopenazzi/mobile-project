package it.unibo.noteforall

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibo.noteforall.data.database.NoteForAllDatabase
import it.unibo.noteforall.data.repositories.ThemeRepository
import it.unibo.noteforall.ui.screen.editProfile.EditProfileViewModel
import it.unibo.noteforall.ui.screen.newNote.NewNoteViewModel
import it.unibo.noteforall.ui.screen.settings.ThemeViewModel
import it.unibo.noteforall.utils.LocationService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    single { get<Context>().dataStore }

    single { ThemeRepository(get()) }

    single { LocationService(get()) }

    val db = Firebase.firestore

    single { Room.databaseBuilder(
        get(),
        NoteForAllDatabase::class.java,
        "noteforall.db"
    ).fallbackToDestructiveMigration().build()
    }

    viewModel { EditProfileViewModel(db) }

    viewModel { ThemeViewModel(get()) }

    viewModel { NewNoteViewModel() }
}
