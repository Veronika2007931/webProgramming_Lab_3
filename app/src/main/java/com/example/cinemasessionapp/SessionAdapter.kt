package com.example.cinemasessionapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionAdapter(
    private val onItemClick: (MovieSession) -> Unit,
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
        val context = holder.itemView.context
        val currentSession = sessionsList[position]

        holder.tvMovieTitle.text = currentSession.movieTitle

        holder.tvDetails.text = context.getString(
            R.string.template_session_details,
            currentSession.hallName,
            currentSession.movieDate,
            currentSession.dateTime
        )

        holder.tvPrice.text = context.getString(R.string.template_session_price, currentSession.ticketPrice)

        holder.itemView.setOnClickListener { onItemClick(currentSession) }
        holder.btnEdit.setOnClickListener { onEditClick(currentSession) }
        holder.btnDelete.setOnClickListener { onDeleteClick(currentSession) }
    }

    override fun getItemCount(): Int = sessionsList.size

    fun setData(newSessions: List<MovieSession>) {
        this.sessionsList = newSessions
        notifyDataSetChanged()
    }
}