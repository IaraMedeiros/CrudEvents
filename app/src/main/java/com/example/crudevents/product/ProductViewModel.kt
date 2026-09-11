package com.example.crudevents.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.crudevents.AppDatabase
import com.example.crudevents.recipeItem.RecipeItem
import kotlinx.coroutines.launch

/**
 * O ViewModel do Produto gerencia a criação de produtos e seus itens de receita.
 */
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val productDao = db.productDao()
    private val recipeItemDao = db.recipeItemDao()

    val allProducts = productDao.getAllProducts().asLiveData()

    /**
     * Salva o Produto e todos os itens da receita vinculados a ele.
     * Usamos uma coroutine para garantir que tudo seja salvo em ordem.
     */
    fun saveProductWithRecipe(product: Product, items: List<RecipeItem>) = viewModelScope.launch {
        // 1. Salva o produto e pega o ID gerado pelo banco
        val newProductId = productDao.insertProduct(product).toInt()
        
        // 2. Para cada item da receita, associa ao novo ID do produto e salva
        items.forEach { item ->
            val itemWithId = item.copy(productId = newProductId)
            recipeItemDao.insertRecipeItem(itemWithId)
        }
    }

    fun update(product: Product) = viewModelScope.launch {
        productDao.updateProduct(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        productDao.deleteProduct(product)
    }
}
