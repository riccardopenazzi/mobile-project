package it.unibo.noteforall.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserId(): User

    @Upsert
    suspend fun insertUserId(user: User)
}