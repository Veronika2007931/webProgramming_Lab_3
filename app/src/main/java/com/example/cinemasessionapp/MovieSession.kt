package com.example.cinemasessionapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_sessions")
data class MovieSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieTitle: String,
    val hallName: String,
    val movieDate: String,
    val dateTime: String,
    val ticketPrice: Double,
    val duration: Int,
    val description: String
)