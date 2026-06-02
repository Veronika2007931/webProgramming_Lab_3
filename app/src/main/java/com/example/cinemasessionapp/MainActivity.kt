package com.example.cinemasessionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: SessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Підключаємо базу даних
        db = AppDatabase.getDatabase(this)

        // 2. Ініціалізуємо список (RecyclerView)
        val rvSessions = findViewById<RecyclerView>(R.id.rvSessions)
        rvSessions.layoutManager = LinearLayoutManager(this)

        // 3. Налаштовуємо адаптер та обробку кнопок Редагувати й Видалити
        adapter = SessionAdapter(
            onEditClick = { session ->
                // При кліку на редагування — відкриваємо друге вікно й передаємо ID сеансу
                val intent = Intent(this, EditSessionActivity::class.java)
                intent.putExtra("SESSION_ID", session.id)
                startActivity(intent)
            },
            onDeleteClick = { session ->
                // При кліку на видалення — видаляємо з БД та оновлюємо екран
                try {
                    db.sessionDao().deleteSession(session)
                    Toast.makeText(this, getString(R.string.msg_session_deleted), Toast.LENGTH_SHORT).show()
                    loadSessionsFromDatabase()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error_general), Toast.LENGTH_SHORT).show()
                }
            }
        )
        rvSessions.adapter = adapter

        // 4. Кнопка додавання нового сеансу (плюсик)
        val fabAddSession = findViewById<FloatingActionButton>(R.id.fabAddSession)
        fabAddSession.setOnClickListener {
            val intent = Intent(this, EditSessionActivity::class.java)
            startActivity(intent)
        }
    }

    // Оновлюємо дані на екрані щоразу, коли повертаємось у це вікно
    override fun onResume() {
        super.onResume()
        loadSessionsFromDatabase()
    }

    // Функція, яка витягує всі записи з SQLite
    private fun loadSessionsFromDatabase() {
        try {
            val sessions = db.sessionDao().getAllSessions()
            adapter.setData(sessions)
        } catch (e: Exception) {
            Toast.makeText(this, "Помилка в опрацюванні запиту", Toast.LENGTH_SHORT).show()
        }
    }
}