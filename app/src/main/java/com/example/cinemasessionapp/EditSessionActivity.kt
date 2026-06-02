package com.example.cinemasessionapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditSessionActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var sessionId: Int = 0
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_session)

        db = AppDatabase.getDatabase(this)

        // 1. Ініціалізуємо ВСІ поля введення з XML розмітки
        val etMovieTitle = findViewById<EditText>(R.id.etMovieTitle)
        val etHallName = findViewById<EditText>(R.id.etHallName)
        val etDateTime = findViewById<EditText>(R.id.etDateTime)
        val etTicketPrice = findViewById<EditText>(R.id.etTicketPrice)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // 2. Перевіряємо, чи ми прийшли сюди для редагування існуючого сеансу
        if (intent.hasExtra("SESSION_ID")) {
            isEditMode = true
            sessionId = intent.getIntExtra("SESSION_ID", 0)

            val session = db.sessionDao().getSessionById(sessionId)
            session?.let {
                etMovieTitle.setText(it.movieTitle)
                etHallName.setText(it.hallName)
                etDateTime.setText(it.dateTime)
                etTicketPrice.setText(it.ticketPrice.toString())
                etDuration.setText(it.duration.toString())
                etDescription.setText(it.description)
            }
        }

        // 3. Обробка натискання кнопки збереження
        btnSave.setOnClickListener {
            val title = etMovieTitle.text.toString().trim()
            val hall = etHallName.text.toString().trim()
            val time = etDateTime.text.toString().trim()
            val priceStr = etTicketPrice.text.toString().trim()
            val durationStr = etDuration.text.toString().trim()
            val desc = etDescription.text.toString().trim()

            // Перевірка на порожні текстові поля
            if (title.isEmpty() || hall.isEmpty() || time.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // Безпечне перетворення числових значень
            val price = priceStr.toDoubleOrNull()
            val duration = durationStr.toIntOrNull()

            // Перевірка коректності числових даних
            if (price == null || price <= 0 || duration == null || duration <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_data), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Створюємо об'єкт сутності, куди тепер успішно передаються всі 7 полів
            val session = MovieSession(
                id = if (isEditMode) sessionId else 0,
                movieTitle = title,
                hallName = hall,
                dateTime = time,
                ticketPrice = price,
                duration = duration,
                description = desc
            )

            // Запис або оновлення даних у SQLite
            if (isEditMode) {
                db.sessionDao().updateSession(session)
            } else {
                db.sessionDao().insertSession(session)
            }

            finish() // Закриваємо вікно й повертаємось на головний екран
        }
    }
}