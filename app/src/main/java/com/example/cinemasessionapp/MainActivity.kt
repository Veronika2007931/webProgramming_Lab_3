package com.example.cinemasessionapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: SessionAdapter
    private lateinit var tvEmptyState: TextView
    private lateinit var rvSessions: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        rvSessions = findViewById(R.id.rvSessions)

        rvSessions.layoutManager = LinearLayoutManager(this)

        adapter = SessionAdapter(
            onItemClick = { session ->
                val intent = Intent(this, SessionDetailActivity::class.java).apply {
                    putExtra(AppDatabase.KEY_SESSION_ID, session.id)
                }
                startActivity(intent)
            },
            onEditClick = { session ->
                val intent = Intent(this, EditSessionActivity::class.java).apply {
                    putExtra(AppDatabase.KEY_SESSION_ID, session.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { session ->
                try {
                    db.sessionDao().deleteSession(session)
                    Toast.makeText(this, getString(R.string.msg_session_deleted), Toast.LENGTH_SHORT).show()
                    loadSessionsFromDatabase()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error_db_load), Toast.LENGTH_SHORT).show()
                }
            }
        )
        rvSessions.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddSession).setOnClickListener {
            startActivity(Intent(this, EditSessionActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadSessionsFromDatabase()
    }

    private fun loadSessionsFromDatabase() {
        try {
            val sessions = db.sessionDao().getAllSessions()
            adapter.setData(sessions)

            if (sessions.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvSessions.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvSessions.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_db_load), Toast.LENGTH_LONG).show()
        }
    }
}