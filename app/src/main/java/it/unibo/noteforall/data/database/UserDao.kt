package it.unibo.noteforall.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserId(): User

    @Upsert
    suspend fun insertUserId(user: User)

    @Delete
    suspend fun deleteUserId(user: User)
}