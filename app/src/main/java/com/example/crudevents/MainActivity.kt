package com.example.crudevents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventViewModel
import com.example.crudevents.events.EventsAdapter
import com.example.crudevents.ingredients.IngredientsActivity
import com.example.crudevents.product.ProductsActivity
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventsAdapter
    private lateinit var txtProximoEvento: TextView
    private lateinit var txtProximoDescricao: TextView
    private lateinit var txtListaTitulo: TextView
    private lateinit var btnToggleHistorico: Button
    
    private val viewModel: EventViewModel by viewModels()
    private val productViewModel: com.example.crudevents.product.ProductViewModel by viewModels()
    private val ingredientViewModel: com.example.crudevents.ingredients.IngredientViewModel by viewModels()

    private var mostrandoHistorico = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtProximoEvento = findViewById(R.id.txtProximoEvento)
        txtProximoDescricao = findViewById(R.id.txtProximoDescricao)
        txtListaTitulo = findViewById(R.id.txtListaTitulo)
        btnToggleHistorico = findViewById(R.id.btnToggleHistorico)
        recyclerView = findViewById(R.id.recyclerViewEventos)

        val txtProximoDia = findViewById<TextView>(R.id.txtProximoDia)
        val txtProximoMes = findViewById<TextView>(R.id.txtProximoMes)

        val txtKpiEventosCount = findViewById<TextView>(R.id.txtKpiEventosCount)
        val txtKpiFaturamento = findViewById<TextView>(R.id.txtKpiFaturamento)
        val txtKpiProdutosCount = findViewById<TextView>(R.id.txtKpiProdutosCount)
        val txtKpiEstoqueCount = findViewById<TextView>(R.id.txtKpiEstoqueCount)
        
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EventsAdapter(
            onEdit = { event ->
                val intent = Intent(this, AddEventActivity::class.java).apply {
                    putExtra("EVENT_ID", event.id)
                    putExtra("EVENT_NOME", event.nome)
                    putExtra("EVENT_DATA", event.data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
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

        // Alterna entre lista de Próximos e Histórico
        btnToggleHistorico.setOnClickListener {
            mostrandoHistorico = !mostrandoHistorico
            atualizarInterfaceLista()
        }

        // Observa a lista de futuros por padrão e atualiza KPI
        viewModel.upcomingEvents.observe(this) { events ->
            if (!mostrandoHistorico) adapter.updateEvents(events)
            txtKpiEventosCount.text = events.size.toString()
        }

        // Observa o histórico
        viewModel.pastEvents.observe(this) { events ->
            if (mostrandoHistorico) adapter.updateEvents(events)
        }

        // Observer do card de destaque (sempre mostra o próximo real)
        viewModel.proximoEvento.observe(this) { event ->
            if (event == null) {
                txtProximoDia.text = "--"
                txtProximoMes.text = "PROX"
                txtProximoEvento.text = "Sem eventos futuros"
                txtProximoDescricao.text = "Crie um evento com data de hoje ou maior"
            } else {
                txtProximoDia.text = "%02d".format(event.data.dayOfMonth)
                val monthStr = when(event.data.monthValue) {
                    1 -> "JAN"; 2 -> "FEV"; 3 -> "MAR"; 4 -> "ABR"; 5 -> "MAI"; 6 -> "JUN"
                    7 -> "JUL"; 8 -> "AGO"; 9 -> "SET"; 10 -> "OUT"; 11 -> "NOV"; 12 -> "DEZ"
                    else -> "MÊS"
                }
                txtProximoMes.text = monthStr
                txtProximoEvento.text = event.nome
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                txtProximoDescricao.text = "${event.data.format(formatter)} • ${event.publicoEstimado} pessoas"
            }
        }

        // KPIs complementares
        productViewModel.allProducts.observe(this) { products ->
            txtKpiProdutosCount.text = products.size.toString()
        }

        ingredientViewModel.allIngredients.observe(this) { ingredients ->
            txtKpiEstoqueCount.text = ingredients.size.toString()
        }

        viewModel.allEventProducts.observe(this) { eventProducts ->
            val products = productViewModel.allProducts.value ?: emptyList()
            val financial = com.example.crudevents.utils.EventCalculator.calculateFinancials(eventProducts, products)
            txtKpiFaturamento.text = "R$ %.2f".format(financial.receitaEstimada)
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

        findViewById<Button>(R.id.btnSeedData).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Carregar Dados de Teste")
                .setMessage("Deseja gerar a massa de dados para o Food Truck (Churros & Tapioca)? Isso irá substituir os dados atuais.")
                .setPositiveButton("Sim, Gerar") { _, _ ->
                    lifecycleScope.launch {
                        com.example.crudevents.utils.DatabaseSeeder.seedDatabase(this@MainActivity, forceRecreate = true)
                        android.widget.Toast.makeText(this@MainActivity, "Massa de teste carregada com sucesso!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun atualizarInterfaceLista() {
        if (mostrandoHistorico) {
            txtListaTitulo.text = "Histórico de Eventos"
            btnToggleHistorico.text = "Ver Próximos"
            // Força a atualização da lista com o que estiver no LiveData de passados
            viewModel.pastEvents.value?.let { adapter.updateEvents(it) }
        } else {
            txtListaTitulo.text = "Próximos Compromissos"
            btnToggleHistorico.text = "Ver Histórico"
            // Força a atualização da lista com o que estiver no LiveData de futuros
            viewModel.upcomingEvents.value?.let { adapter.updateEvents(it) }
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
