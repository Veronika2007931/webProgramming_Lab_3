package com.example.cinemasessionapp

import androidx.room.*

@Dao
interface SessionDao {
    @Query("SELECT * FROM movie_sessions")
    fun getAllSessions(): List<MovieSession>

    @Query("SELECT * FROM movie_sessions WHERE id = :id")
    fun getSessionById(id: Int): MovieSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: MovieSession)

    @Update
    fun updateSession(session: MovieSession)

    @Delete
    fun deleteSession(session: MovieSession)
}