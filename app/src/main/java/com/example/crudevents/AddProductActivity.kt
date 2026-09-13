package com.example.crudevents

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.R
import com.example.crudevents.ingredients.IngredientViewModel
import com.example.crudevents.product.Product
import com.example.crudevents.product.ProductViewModel
import com.example.crudevents.recipeItem.RecipeItem
import com.example.crudevents.recipeItem.RecipesAdapter

class AddProductActivity : AppCompatActivity() {
    
    private val productViewModel: ProductViewModel by viewModels()
    private val ingredientViewModel: IngredientViewModel by viewModels()
    
    private val recipeItemsTemp = mutableListOf<RecipeItem>()
    private lateinit var adapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val editNome = findViewById<EditText>(R.id.editNome)
        val editPreco = findViewById<EditText>(R.id.editPreco)
        val recycler = findViewById<RecyclerView>(R.id.recyclerIngredientes)
        
        // Passamos a lista original para o Adapter
        adapter = RecipesAdapter(recipeItemsTemp) { position ->
            recipeItemsTemp.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
        
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        ingredientViewModel.allIngredients.observe(this) { lista ->
            adapter.updateAvailableIngredients(lista)
        }

        findViewById<Button>(R.id.btnAdicionarIngrediente).setOnClickListener {
            recipeItemsTemp.add(RecipeItem(productId = 0, ingredientId = 0, quantidade = 0.0))
            adapter.notifyItemInserted(recipeItemsTemp.size - 1)
        }

        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            // Antes de salvar, garantimos que o foco saiu de qualquer campo para gravar o último valor digitado
            currentFocus?.clearFocus()

            val nome = editNome.text.toString()
            val preco = editPreco.text.toString().toDoubleOrNull() ?: 0.0
            val novoProduto = Product(nome = nome, preco = preco)
            
            productViewModel.saveProductWithRecipe(novoProduto, recipeItemsTemp)
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
