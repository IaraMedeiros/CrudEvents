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
        holder.binding.txtQuantidadeEstoque.text = "%.1f".format(ingredient.quantidadeEstoque)
        holder.binding.txtUnidade.text = ingredient.unidade

        val icon = when {
            ingredient.nome.lowercase().contains("farinha") || ingredient.nome.lowercase().contains("goma") -> "🌾"
            ingredient.nome.lowercase().contains("doce") || ingredient.nome.lowercase().contains("nutella") || ingredient.nome.lowercase().contains("leite") -> "🍯"
            ingredient.nome.lowercase().contains("frango") || ingredient.nome.lowercase().contains("carne") || ingredient.nome.lowercase().contains("queijo") -> "🧀"
            else -> "📦"
        }
        holder.binding.txtIconeIngrediente.text = icon

        val context = holder.itemView.context
        val isLow = ingredient.quantidadeEstoque <= 5.0
        if (isLow) {
            holder.binding.txtTagEstoqueStatus.text = "Baixo"
            holder.binding.txtTagEstoqueStatus.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_orange_text))
            holder.binding.txtTagEstoqueStatus.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_orange_bg))
        } else {
            holder.binding.txtTagEstoqueStatus.text = "Normal"
            holder.binding.txtTagEstoqueStatus.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_green_text))
            holder.binding.txtTagEstoqueStatus.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, com.example.crudevents.R.color.status_green_bg))
        }

        holder.binding.btnEdit.setOnClickListener { onEdit(ingredient) }
        holder.binding.btnDelete.setOnClickListener { onDelete(ingredient) }
    }

    override fun getItemCount() = ingredients.size

    class IngredientHolder(val binding: AdapterIngredientBinding)
        : RecyclerView.ViewHolder(binding.root)
}
