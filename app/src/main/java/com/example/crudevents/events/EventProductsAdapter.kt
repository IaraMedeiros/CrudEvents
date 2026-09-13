package com.example.crudevents.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterRecipeItemBinding
import com.example.crudevents.product.Product

class EventProductsAdapter(
    private val items: MutableList<EventProduct>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<EventProductsAdapter.ViewHolder>() {

    private var availableProducts: List<Product> = emptyList()

    fun updateAvailableProducts(products: List<Product>) {
        this.availableProducts = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val productNames = availableProducts.map { it.nome }
        val productAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, productNames)
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.spinnerIngrediente.adapter = productAdapter

        val productIndex = availableProducts.indexOfFirst { it.id == item.productId }
        if (productIndex != -1) holder.binding.spinnerIngrediente.setSelection(productIndex)

        val qtdStr = if (item.quantidade > 0) item.quantidade.toString() else ""
        if (holder.binding.editQuantidade.text.toString() != qtdStr) {
            holder.binding.editQuantidade.setText(qtdStr)
        }

        holder.binding.spinnerUnidade.visibility = View.GONE

        holder.binding.editQuantidade.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val valor = holder.binding.editQuantidade.text.toString().toDoubleOrNull() ?: 0.0
                    items[position] = items[position].copy(quantidade = valor)
                }
            }
        }

        holder.binding.spinnerIngrediente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && availableProducts.isNotEmpty()) {
                    items[position] = items[position].copy(productId = availableProducts[pos].id)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        holder.binding.btnDelete.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onDelete(position)
            }
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(val binding: AdapterRecipeItemBinding) : RecyclerView.ViewHolder(binding.root)
}
