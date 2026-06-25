package com.example.crudevents.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterEventBinding
import java.time.format.DateTimeFormatter

/**
 * O Adapter é o responsável por transformar a lista de dados (objetos Event)
 * em elementos visuais que o usuário vê na tela (as "Cartinhas" ou Cards).
 */
class EventsAdapter(
    private var events: List<Event> = emptyList(),
    private val onEdit: (Event) -> Unit,   // Função que será chamada ao clicar em Editar
    private val onDelete: (Event) -> Unit  // Função que será chamada ao clicar em Excluir
) : RecyclerView.Adapter<EventsAdapter.EventHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Atualiza a lista interna de eventos e redesenha a tela.
     */
    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    /**
     * Cria o "esqueleto" visual de uma linha da lista (infla o XML).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = AdapterEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventHolder(binding)
    }

    /**
     * Pega os dados de um Evento e coloca nos campos de texto da "Cartinha".
     */
    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = events[position]

        holder.binding.txtNome.text = event.nome
        holder.binding.txtData.text = event.data.format(formatter)
        holder.binding.txtLocal.text = event.local
        holder.binding.txtPublico.text = event.publicoEstimado.toString()

        // Configura o clique nos botões de Editar e Excluir
        holder.binding.btnEdit.setOnClickListener { onEdit(event) }
        holder.binding.btnDelete.setOnClickListener { onDelete(event) }
    }

    override fun getItemCount() = events.size

    /**
     * O ViewHolder guarda as referências para os componentes visuais de uma linha.
     */
    class EventHolder(val binding: AdapterEventBinding)
        : RecyclerView.ViewHolder(binding.root)
}