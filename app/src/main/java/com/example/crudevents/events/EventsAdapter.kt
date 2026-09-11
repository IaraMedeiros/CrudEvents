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

        holder.binding.btnEdit.setOnClickListener { onEdit(event) }
        holder.binding.btnDelete.setOnClickListener { onDelete(event) }
    }

    override fun getItemCount() = events.size

    class EventHolder(val binding: AdapterEventBinding)
        : RecyclerView.ViewHolder(binding.root)
}
