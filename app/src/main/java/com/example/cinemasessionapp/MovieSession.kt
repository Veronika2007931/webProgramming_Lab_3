package com.example.cinemasessionapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_sessions")
data class MovieSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieTitle: String,
    val hallName: String,
    val movieDate: String, // Нове поле для дати
    val dateTime: String,  // Це залишається для часу (напр. "18:00")
    val ticketPrice: Double,
    val duration: Int,
    val description: String
)