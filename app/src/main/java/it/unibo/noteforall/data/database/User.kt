package it.unibo.noteforall.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    var userId: String,
    var userLong: Double,
    var userLat: Double
)
