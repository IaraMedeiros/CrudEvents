package com.example.crudevents

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventProduct
import com.example.crudevents.events.EventProductsAdapter
import com.example.crudevents.events.EventViewModel
import com.example.crudevents.ingredients.IngredientViewModel
import com.example.crudevents.product.ProductViewModel
import com.example.crudevents.recipeItem.RecipeViewModel
import com.example.crudevents.utils.EventCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

    private val viewModel: EventViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

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

        // Mantém os LiveDatas aquecidos para cálculos imediatos
        productViewModel.allProducts.observe(this) { products ->
            adapter.updateAvailableProducts(products)
        }
        ingredientViewModel.allIngredients.observe(this) {}
        recipeViewModel.allRecipeItems.observe(this) {}
        viewModel.allEvents.observe(this) {}
        viewModel.allEventProducts.observe(this) {}

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

        // Botão de Previsão por IA / Histórico
        findViewById<Button>(R.id.btnSugerirIA).setOnClickListener {
            val publicoEstimado = publico.text.toString().toIntOrNull() ?: 0
            if (publicoEstimado <= 0) {
                Toast.makeText(this, "Informe o público estimado primeiro!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pastEvents = viewModel.allEvents.value?.filter { it.data < LocalDate.now() } ?: emptyList()
            val pastEventProducts = viewModel.allEventProducts.value ?: emptyList()

            if (pastEvents.isEmpty()) {
                Toast.makeText(this, "Nenhum evento no histórico para gerar previsão. Cadastre eventos passados!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val predictions = EventCalculator.predictProductDemandForPublic(publicoEstimado, pastEvents, pastEventProducts)
            if (predictions.isEmpty()) {
                Toast.makeText(this, "Não há dados de consumo nos eventos passados.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            eventProductsTemp.clear()
            predictions.forEach { (prodId, qte) ->
                eventProductsTemp.add(EventProduct(eventId = if (eventId != -1) eventId else 0, productId = prodId, quantidade = qte.coerceAtLeast(1.0)))
            }
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "✨ Previsão gerada para $publicoEstimado pessoas com base no histórico!", Toast.LENGTH_LONG).show()
        }

        // Botão de Calcular Ingredientes Necessários e Lucro Estimado
        findViewById<Button>(R.id.btnCalcularIngredientes).setOnClickListener {
            currentFocus?.clearFocus()
            val validProducts = eventProductsTemp.filter { it.productId != 0 && it.quantidade > 0 }
            if (validProducts.isEmpty()) {
                Toast.makeText(this, "Adicione e selecione a quantidade de pelo menos um produto.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val recipes = recipeViewModel.allRecipeItems.value ?: emptyList()
            val ingredients = ingredientViewModel.allIngredients.value ?: emptyList()
            val products = productViewModel.allProducts.value ?: emptyList()

            val financial = EventCalculator.calculateFinancials(validProducts, products)
            val requiredIngredients = EventCalculator.calculateRequiredIngredients(validProducts, recipes, ingredients)

            val sb = StringBuilder()
            sb.append("💰 ESTIMATIVA FINANCEIRA:\n")
            sb.append("Receita / Faturamento Estimado: R$ %.2f\n\n".format(financial.receitaEstimada))
            sb.append("📦 NECESSIDADE DE INGREDIENTES:\n")

            if (requiredIngredients.isEmpty()) {
                sb.append("Nenhum ingrediente cadastrado para os produtos selecionados.\n(Cadastre a ficha técnica nos Produtos).")
            } else {
                requiredIngredients.forEach { req ->
                    val statusEstoque = if (req.faltaNoEstoque > 0) {
                        "⚠️ Faltam %.2f %s no estoque".format(req.faltaNoEstoque, req.unidade)
                    } else {
                        "✅ Estoque suficiente"
                    }
                    sb.append("• %s: %.2f %s\n  (Estoque: %.2f %s | %s)\n\n".format(
                        req.ingredientName, req.quantidadeNecessaria, req.unidade,
                        req.quantidadeEstoque, req.unidade, statusEstoque
                    ))
                }
            }

            AlertDialog.Builder(this)
                .setTitle("🧮 Cálculo do Evento")
                .setMessage(sb.toString())
                .setPositiveButton("Entendido", null)
                .show()
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
