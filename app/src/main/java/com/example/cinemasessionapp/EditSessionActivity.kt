package com.example.cinemasessionapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EditSessionActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var sessionId: Int = 0
    private var isEditMode = false

    companion object {
        private const val MAX_HALL_COUNT = 12
        private const val DATE_PATTERN = "dd.MM.yyyy"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_session)

        db = AppDatabase.getDatabase(this)

        val etMovieTitle = findViewById<EditText>(R.id.etMovieTitle)
        val etHallName = findViewById<EditText>(R.id.etHallName)
        val etMovieDate = findViewById<EditText>(R.id.etMovieDate)
        val etDateTime = findViewById<EditText>(R.id.etDateTime)
        val etTicketPrice = findViewById<EditText>(R.id.etTicketPrice)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)

        if (intent.hasExtra(AppDatabase.KEY_SESSION_ID)) {
            isEditMode = true
            sessionId = intent.getIntExtra(AppDatabase.KEY_SESSION_ID, 0)

            db.sessionDao().getSessionById(sessionId)?.let {
                etMovieTitle.setText(it.movieTitle)
                etHallName.setText(it.hallName)
                etMovieDate.setText(it.movieDate)
                etDateTime.setText(it.dateTime)
                etTicketPrice.setText(it.ticketPrice.toString())
                etDuration.setText(it.duration.toString())
                etDescription.setText(it.description)
            }
        }

        btnSave.setOnClickListener {
            val title = etMovieTitle.text.toString().trim()
            val hall = etHallName.text.toString().trim()
            val dateStr = etMovieDate.text.toString().trim()
            val time = etDateTime.text.toString().trim()
            val priceStr = etTicketPrice.text.toString().trim()
            val durationStr = etDuration.text.toString().trim()
            val desc = etDescription.text.toString().trim()

            if (title.isEmpty() || hall.isEmpty() || dateStr.isEmpty() || time.isEmpty() ||
                priceStr.isEmpty() || durationStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()
            val duration = durationStr.toIntOrNull()

            if (price == null || price <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_price), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (duration == null || duration <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_duration), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val hallNumber = hall.filter { it.isDigit() }.toIntOrNull()
            if (hallNumber != null && hallNumber > MAX_HALL_COUNT) {
                Toast.makeText(this, getString(R.string.error_invalid_hall), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            try {
                val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
                val inputDate = LocalDate.parse(dateStr, formatter)
                if (inputDate.isBefore(LocalDate.now())) {
                    Toast.makeText(this, getString(R.string.error_past_date), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } catch (e: DateTimeParseException) {
                Toast.makeText(this, getString(R.string.error_date_format), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val isDuplicate = db.sessionDao().isSessionDuplicate(
                title, hall, dateStr, time, price, duration, desc
            )

            if (isDuplicate && !isEditMode) {
                Toast.makeText(this, getString(R.string.error_duplicate_session), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val session = MovieSession(
                id = if (isEditMode) sessionId else 0,
                movieTitle = title, hallName = hall, movieDate = dateStr, dateTime = time,
                ticketPrice = price, duration = duration, description = desc
            )

            if (isEditMode) {
                db.sessionDao().updateSession(session)
                Toast.makeText(this, getString(R.string.msg_session_updated), Toast.LENGTH_SHORT).show()
            } else {
                db.sessionDao().insertSession(session)
                Toast.makeText(this, getString(R.string.msg_session_added), Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}