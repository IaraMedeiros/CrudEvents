package com.example.crudevents.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterProductBinding

class ProductsAdapter(
    private var products: List<Product> = emptyList(),
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    fun updateProducts(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding = AdapterProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.binding.txtNome.text = product.nome
        holder.binding.txtPreco.text = product.preco.toString()

        holder.binding.btnEdit.setOnClickListener { onEdit(product) }
        holder.binding.btnDelete.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount() = products.size

    class ProductHolder(val binding: AdapterProductBinding)
        : RecyclerView.ViewHolder(binding.root)
}
