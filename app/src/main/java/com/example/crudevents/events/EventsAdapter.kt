package com.example.crudevents.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterEventBinding
import java.time.format.DateTimeFormatter

class EventsAdapter(
    private var events: List<Event> = emptyList(),
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = AdapterEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = events[position]

        holder.binding.txtNome.text = event.nome
        holder.binding.txtData.text = event.data.format(formatter)
        holder.binding.txtLocal.text = event.local
        holder.binding.txtPublico.text = event.publicoEstimado.toString()

        holder.binding.txtDiaBadge.text = "%02d".format(event.data.dayOfMonth)
        val monthStr = when(event.data.monthValue) {
            1 -> "JAN"; 2 -> "FEV"; 3 -> "MAR"; 4 -> "ABR"; 5 -> "MAI"; 6 -> "JUN"
            7 -> "JUL"; 8 -> "AGO"; 9 -> "SET"; 10 -> "OUT"; 11 -> "NOV"; 12 -> "DEZ"
            else -> "MÊS"
        }
        holder.binding.txtMesBadge.text = monthStr

        val isUpcoming = event.data >= java.time.LocalDate.now()
        holder.binding.txtTagStatus.text = if (isUpcoming) "Confirmado" else "Histórico"
        val context = holder.itemView.context
        if (isUpcoming) {
            holder.binding.txtTagStatus.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_green_text))
            holder.binding.txtTagStatus.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_green_bg))
        } else {
            holder.binding.txtTagStatus.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_orange_text))
            holder.binding.txtTagStatus.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_orange_bg))
        }

        holder.binding.btnEdit.setOnClickListener { onEdit(event) }
        holder.binding.btnDelete.setOnClickListener { onDelete(event) }
    }

    override fun getItemCount() = events.size

    class EventHolder(val binding: AdapterEventBinding)
        : RecyclerView.ViewHolder(binding.root)
}
