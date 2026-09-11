package com.example.crudevents.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterIngredientBinding

class IngredientsAdapter(
    private var ingredients: List<Ingredient> = emptyList(),
    private val onEdit: (Ingredient) -> Unit,
    private val onDelete: (Ingredient) -> Unit
) : RecyclerView.Adapter<IngredientsAdapter.IngredientHolder>() {

    fun updateIngredients(newIngredients: List<Ingredient>) {
        this.ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val binding = AdapterIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        val ingredient = ingredients[position]

        holder.binding.txtNome.text = ingredient.nome
        holder.binding.txtQuantidadeEstoque.text = ingredient.quantidadeEstoque.toString()
        holder.binding.txtUnidade.text = ingredient.unidade

        holder.binding.btnEdit.setOnClickListener { onEdit(ingredient) }
        holder.binding.btnDelete.setOnClickListener { onDelete(ingredient) }
    }

    override fun getItemCount() = ingredients.size

    class IngredientHolder(val binding: AdapterIngredientBinding)
        : RecyclerView.ViewHolder(binding.root)
}
