package com.example.crudevents.recipeItem

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterRecipeItemBinding
import com.example.crudevents.ingredients.Ingredient

class RecipesAdapter(
    private val recipeItems: MutableList<RecipeItem>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<RecipesAdapter.RecipeItemHolder>() {

    private var allIngredients: List<Ingredient> = emptyList()

    fun updateAvailableIngredients(ingredients: List<Ingredient>) {
        this.allIngredients = ingredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemHolder {
        val binding = AdapterRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeItemHolder, position: Int) {
        val item = recipeItems[position]

        // 1. Configura Unidades
        val unidades = listOf("g", "kg", "ml", "L", "un")
        val unitAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, unidades)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.spinnerUnidade.adapter = unitAdapter
        holder.binding.spinnerUnidade.setSelection(unidades.indexOf(item.unidade).coerceAtLeast(0))

        // 2. Configura Ingredientes
        val ingredientNames = allIngredients.map { it.nome }
        val ingredientAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, ingredientNames)
        ingredientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.spinnerIngrediente.adapter = ingredientAdapter
        
        val ingIndex = allIngredients.indexOfFirst { it.id == item.ingredientId }
        if (ingIndex != -1) holder.binding.spinnerIngrediente.setSelection(ingIndex)

        // 3. Quantidade (Evita disparar o listener infinitamente)
        val qtdStr = if (item.quantidade > 0) item.quantidade.toString() else ""
        if (holder.binding.editQuantidade.text.toString() != qtdStr) {
            holder.binding.editQuantidade.setText(qtdStr)
        }

        // --- LISTENERS PARA SALVAR NA LISTA ORIGINAL DA ACTIVITY ---

        holder.binding.editQuantidade.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Salva quando o usuário sai do campo
                val valor = holder.binding.editQuantidade.text.toString().toDoubleOrNull() ?: 0.0
                recipeItems[holder.adapterPosition] = recipeItems[holder.adapterPosition].copy(quantidade = valor)
            }
        }

        holder.binding.spinnerIngrediente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (allIngredients.isNotEmpty()) {
                    recipeItems[holder.adapterPosition] = recipeItems[holder.adapterPosition].copy(ingredientId = allIngredients[pos].id)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        holder.binding.spinnerUnidade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                recipeItems[holder.adapterPosition] = recipeItems[holder.adapterPosition].copy(unidade = unidades[pos])
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        holder.binding.btnDelete.setOnClickListener { onDelete(holder.adapterPosition) }
    }

    override fun getItemCount() = recipeItems.size

    class RecipeItemHolder(val binding: AdapterRecipeItemBinding) : RecyclerView.ViewHolder(binding.root)
}
