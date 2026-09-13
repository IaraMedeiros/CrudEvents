package com.example.crudevents.recipeItem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.crudevents.AppDatabase
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeItemDao = AppDatabase.getDatabase(application).recipeItemDao()

    val allRecipeItems = recipeItemDao.getAllRecipeItems().asLiveData()

    fun insert(recipeItem: RecipeItem) = viewModelScope.launch {
        recipeItemDao.insertRecipeItem(recipeItem)
    }

    fun update(recipeItem: RecipeItem) = viewModelScope.launch {
        recipeItemDao.updateRecipeItem(recipeItem)
    }

    fun delete(recipeItem: RecipeItem) = viewModelScope.launch {
        recipeItemDao.deleteRecipeItem(recipeItem)
    }
}
