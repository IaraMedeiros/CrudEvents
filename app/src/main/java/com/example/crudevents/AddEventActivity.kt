package com.example.crudevents

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventProduct
import com.example.crudevents.events.EventProductsAdapter
import com.example.crudevents.events.EventViewModel
import com.example.crudevents.product.ProductViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

    private val viewModel: EventViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    
    private val eventProductsTemp = mutableListOf<EventProduct>()
    private lateinit var adapter: EventProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val nome = findViewById<EditText>(R.id.editNome)
        val data = findViewById<EditText>(R.id.editData)
        val local = findViewById<EditText>(R.id.editLocal)
        val publico = findViewById<EditText>(R.id.editPublico)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val recycler = findViewById<RecyclerView>(R.id.recyclerProdutosEvento)
        
        adapter = EventProductsAdapter(eventProductsTemp) { position ->
            eventProductsTemp.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        productViewModel.allProducts.observe(this) { products ->
            adapter.updateAvailableProducts(products)
        }

        data.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(this, { _, ano, mes, dia ->
                data.setText("%02d/%02d/%04d".format(dia, mes + 1, ano))
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
        }

        val eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId != -1) {
            title = "Editar Evento"
            nome.setText(intent.getStringExtra("EVENT_NOME"))
            data.setText(intent.getStringExtra("EVENT_DATA"))
            local.setText(intent.getStringExtra("EVENT_LOCAL"))
            publico.setText(intent.getIntExtra("EVENT_PUBLICO", 0).toString())
            btnSalvar.text = "Atualizar Evento"
            
            viewModel.getProductsForEvent(eventId).observe(this) { items ->
                if (eventProductsTemp.isEmpty()) {
                    eventProductsTemp.addAll(items)
                    adapter.notifyDataSetChanged()
                }
            }
        } else {
            title = "Novo Evento"
        }

        findViewById<Button>(R.id.btnAdicionarProduto).setOnClickListener {
            eventProductsTemp.add(EventProduct(eventId = if (eventId != -1) eventId else 0, productId = 0, quantidade = 0.0))
            adapter.notifyItemInserted(eventProductsTemp.size - 1)
        }

        btnSalvar.setOnClickListener {
            currentFocus?.clearFocus()

            val dateStr = data.text.toString()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val localDate = try { LocalDate.parse(dateStr, formatter) } catch (e: Exception) { LocalDate.now() }

            val event = Event(
                id = if (eventId != -1) eventId else 0,
                nome = nome.text.toString(),
                data = localDate,
                local = local.text.toString(),
                publicoEstimado = publico.text.toString().toIntOrNull() ?: 0
            )

            viewModel.saveEventWithProducts(event, eventProductsTemp)
            finish()
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
