package com.example.cinemasessionapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter(
    private val onEditClick: (MovieSession) -> Unit,
    private val onDeleteClick: (MovieSession) -> Unit
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private var sessionsList = emptyList<MovieSession>()

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMovieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val currentSession = sessionsList[position]

        // Тут ми динамічно вставляємо дані з БД в XML без жодного хардкоду
        holder.tvMovieTitle.text = currentSession.movieTitle
        holder.tvDetails.text = "Зал: ${currentSession.hallName} | Час: ${currentSession.dateTime}"
        holder.tvPrice.text = "Ціна: ${currentSession.ticketPrice} грн"

        // Обробка кліків на кнопки редагування та видалення
        holder.btnEdit.setOnClickListener { onEditClick(currentSession) }
        holder.btnDelete.setOnClickListener { onDeleteClick(currentSession) }
    }

    override fun getItemCount(): Int = sessionsList.size

    // Метод для оновлення списку даних
    fun setData(newSessions: List<MovieSession>) {
        this.sessionsList = newSessions
        notifyDataSetChanged()
    }
}