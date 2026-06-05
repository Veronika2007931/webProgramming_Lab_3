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

        if (intent.hasExtra("SESSION_ID")) {
            isEditMode = true
            sessionId = intent.getIntExtra("SESSION_ID", 0)

            val session = db.sessionDao().getSessionById(sessionId)
            session?.let {
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
                Toast.makeText(this, "Не вдалося зберегти: заповніть усі поля!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            val price = priceStr.toDoubleOrNull()
            val duration = durationStr.toIntOrNull()


            if (price == null || price <= 0) {
                Toast.makeText(this, "Помилка: Ціна квитка має бути більшою за 0 грн!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (duration == null || duration <= 0) {
                Toast.makeText(this, "Помилка: Тривалість фільму має бути більшою за 0 хв!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val hallNumbers = hall.filter { it.isDigit() }
            val hallNumber = hallNumbers.toIntOrNull()

            if (hallNumber != null && hallNumber > 12) {
                Toast.makeText(this, "Помилка: Номер залу не може бути більшим за 12!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            try {

                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val inputDate = LocalDate.parse(dateStr, formatter)
                val today = LocalDate.now()


                if (inputDate.isBefore(today)) {
                    Toast.makeText(this, "Помилка: Не можна призначити сеанс на минулу дату!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } catch (e: DateTimeParseException) {

                Toast.makeText(this, "Некоректний формат дати! Використовуйте ДД.ММ.РРРР (напр. 04.06.2026)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val isDuplicate = db.sessionDao().isSessionDuplicate(
                title = title,
                hall = hall,
                date = dateStr,
                time = time,
                price = price,
                duration = duration,
                desc = desc
            )

            if (isDuplicate) {
                Toast.makeText(this, "Такий сеанс уже існує в розкладі!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            val session = MovieSession(
                id = if (isEditMode) sessionId else 0,
                movieTitle = title,
                hallName = hall,
                movieDate = dateStr,
                dateTime = time,
                ticketPrice = price,
                duration = duration,
                description = desc
            )

            if (isEditMode) {
                db.sessionDao().updateSession(session)
                Toast.makeText(this, "Сеанс оновлено!", Toast.LENGTH_SHORT).show()
            } else {
                db.sessionDao().insertSession(session)
                Toast.makeText(this, "Сеанс додано!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}