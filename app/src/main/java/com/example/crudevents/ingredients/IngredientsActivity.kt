package com.example.crudevents.ingredients

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudevents.databinding.ActivityIngredientsBinding

class IngredientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientsBinding
    private val viewModel: IngredientViewModel by viewModels()
    private lateinit var adapter: IngredientsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngredientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = IngredientsAdapter(
            onEdit = { ingredient ->
                val intent = Intent(this, AddIngredientActivity::class.java).apply {
                    putExtra("ID", ingredient.id)
                    putExtra("NOME", ingredient.nome)
                    putExtra("QTD", ingredient.quantidadeEstoque)
                    putExtra("UNIDADE", ingredient.unidade)
                }
                startActivity(intent)
            },
            onDelete = { ingredient ->
                showDeleteConfirmation(ingredient)
            }
        )

        binding.recyclerIngredients.layoutManager = LinearLayoutManager(this)
        binding.recyclerIngredients.adapter = adapter

        viewModel.allIngredients.observe(this) { list ->
            adapter.updateIngredients(list)
        }

        binding.btnAddIngredient.setOnClickListener {
            startActivity(Intent(this, AddIngredientActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmation(ingredient: Ingredient) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Ingrediente")
            .setMessage("Deseja excluir ${ingredient.nome}?")
            .setPositiveButton("Sim") { _, _ -> viewModel.delete(ingredient) }
            .setNegativeButton("Não", null)
            .show()
    }
}
