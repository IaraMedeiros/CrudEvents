package com.example.crudevents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventViewModel
import com.example.crudevents.events.EventsAdapter
import com.example.crudevents.ingredients.IngredientsActivity
import com.example.crudevents.product.ProductsActivity
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventsAdapter
    private lateinit var txtProximoEvento: TextView
    private lateinit var txtProximoDescricao: TextView
    
    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtProximoEvento = findViewById(R.id.txtProximoEvento)
        txtProximoDescricao = findViewById(R.id.txtProximoDescricao)
        recyclerView = findViewById(R.id.recyclerViewEventos)
        
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EventsAdapter(
            onEdit = { event ->
                val intent = Intent(this, AddEventActivity::class.java).apply {
                    putExtra("EVENT_ID", event.id)
                    putExtra("EVENT_NOME", event.nome)
                    putExtra(
                        "EVENT_DATA",
                        event.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    )
                    putExtra("EVENT_LOCAL", event.local)
                    putExtra("EVENT_PUBLICO", event.publicoEstimado)
                }
                startActivity(intent)
            },
            onDelete = { event ->
                showDeleteConfirmation(event)
            }
        )
        recyclerView.adapter = adapter

        viewModel.allEvents.observe(this) { events ->
            adapter.updateEvents(events)
        }

        viewModel.proximoEvento.observe(this) { event ->
            if (event == null) {
                txtProximoEvento.text = "Nenhum evento"
                txtProximoDescricao.text = "Cadastre um novo evento no botão +"
            } else {
                txtProximoEvento.text = event.nome
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                txtProximoDescricao.text = "${event.data.format(formatter)} • ${event.publicoEstimado} pessoas"
            }
        }

        findViewById<View>(R.id.btnAddEvent).setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        findViewById<Button>(R.id.btnNavProducts).setOnClickListener {
            startActivity(Intent(this, ProductsActivity::class.java))
        }

        findViewById<Button>(R.id.btnNavIngredients).setOnClickListener {
            startActivity(Intent(this, IngredientsActivity::class.java))
        }
    }

    private fun showDeleteConfirmation(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Evento")
            .setMessage("Tem certeza que deseja excluir o evento \"${event.nome}\"?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.delete(event)
            }
            .setNegativeButton("Não", null)
            .show()
    }
}
