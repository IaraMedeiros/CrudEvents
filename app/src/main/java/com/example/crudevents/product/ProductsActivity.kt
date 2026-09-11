package com.example.crudevents.product

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudevents.AddProductActivity
import com.example.crudevents.databinding.ActivityProductsBinding

class ProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductsBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = ProductsAdapter(
            onEdit = { product ->
                val intent = Intent(this, AddProductActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                    putExtra("PRODUCT_NOME", product.nome)
                    putExtra("PRODUCT_PRECO", product.preco)
                }
                startActivity(intent)
            },
            onDelete = { product ->
                showDeleteConfirmation(product)
            }
        )

        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter

        viewModel.allProducts.observe(this) { list ->
            adapter.updateProducts(list)
        }

        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Produto")
            .setMessage("Deseja excluir ${product.nome}?")
            .setPositiveButton("Sim") { _, _ -> viewModel.delete(product) }
            .setNegativeButton("Não", null)
            .show()
    }
}
