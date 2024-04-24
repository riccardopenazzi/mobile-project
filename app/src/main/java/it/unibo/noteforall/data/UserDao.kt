package it.unibo.noteforall.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserId(): String
}