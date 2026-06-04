package com.example.cinemasessionapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SessionDao {
    @Query("SELECT * FROM movie_sessions")
    fun getAllSessions(): List<MovieSession>

    @Query("SELECT * FROM movie_sessions WHERE id = :id")
    fun getSessionById(id: Int): MovieSession?
    @Query("""
        SELECT EXISTS(
            SELECT * FROM movie_sessions 
            WHERE movieTitle = :title 
              AND hallName = :hall 
              AND movieDate = :date 
              AND dateTime = :time 
              AND ticketPrice = :price 
              AND duration = :duration 
              AND description = :desc
        )
    """)
    fun isSessionDuplicate(
        title: String,
        hall: String,
        date: String,
        time: String,
        price: Double,
        duration: Int,
        desc: String
    ): Boolean
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: MovieSession)

    @Update
    fun updateSession(session: MovieSession)

    @Delete
    fun deleteSession(session: MovieSession)
}