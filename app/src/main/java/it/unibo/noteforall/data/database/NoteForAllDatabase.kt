package it.unibo.noteforall.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [User::class],
    version = 2
)

abstract class NoteForAllDatabase: RoomDatabase() {
    abstract val dao: UserDao
}