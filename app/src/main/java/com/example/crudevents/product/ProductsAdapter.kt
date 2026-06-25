package com.example.crudevents.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crudevents.databinding.AdapterProductBinding
import com.example.crudevents.product.Product
import java.time.format.DateTimeFormatter

/**
 * O Adapter é o responsável por transformar a lista de dados (objetos Product)
 * em elementos visuais que o usuário vê na tela (as "Cartinhas" ou Cards).
 */
class ProductsAdapter(
    private var products: List<Product> = emptyList(),
    private val onEdit: (Product) -> Unit,   // Função que será chamada ao clicar em Editar
    private val onDelete: (Product) -> Unit  // Função que será chamada ao clicar em Excluir
) : RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Atualiza a lista interna de productos e redesenha a tela.
     */
    fun updateProducts(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }

    /**
     * Cria o "esqueleto" visual de uma linha da lista (infla o XML).
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding = AdapterProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductHolder(binding)
    }

    /**
     * Pega os dados de um Producto e coloca nos campos de texto da "Cartinha".
     */
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]

        holder.binding.txtNome.text = product.nome
        holder.binding.txtPreco.text = product.preco

        // Configura o clique nos botões de Editar e Excluir
        holder.binding.btnEdit.setOnClickListener { onEdit(product) }
        holder.binding.btnDelete.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount() = products.size

    /**
     * O ViewHolder guarda as referências para os componentes visuais de uma linha.
     */
    class ProductHolder(val binding: AdapterProductBinding)
        : RecyclerView.ViewHolder(binding.root)
}