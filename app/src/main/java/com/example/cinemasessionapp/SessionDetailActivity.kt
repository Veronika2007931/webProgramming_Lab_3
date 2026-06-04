package com.example.cinemasessionapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SessionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        val db = AppDatabase.getDatabase(this)

        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvHall = findViewById<TextView>(R.id.tvDetailHall)
        val tvDateTime = findViewById<TextView>(R.id.tvDetailDateTime)
        val tvDuration = findViewById<TextView>(R.id.tvDetailDuration)
        val tvPrice = findViewById<TextView>(R.id.tvDetailPrice)
        val tvDescription = findViewById<TextView>(R.id.tvDetailDescription)

        val sessionId = intent.getIntExtra(AppDatabase.KEY_SESSION_ID, 0)

        db.sessionDao().getSessionById(sessionId)?.let {
            tvTitle.text = it.movieTitle
            tvHall.text = getString(R.string.template_detail_hall, it.hallName)
            tvDateTime.text = getString(R.string.template_detail_datetime, it.movieDate, it.dateTime)
            tvDuration.text = getString(R.string.template_detail_duration, it.duration)
            tvPrice.text = getString(R.string.template_detail_price, it.ticketPrice)
            tvDescription.text = it.description
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}