package com.example.crudevents.ingredients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.crudevents.AppDatabase
import kotlinx.coroutines.launch

class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    private val ingredientDao = AppDatabase.getDatabase(application).ingredientDao()

    val allIngredients = ingredientDao.getAllIngredients().asLiveData()

    fun insert(ingredient: Ingredient) = viewModelScope.launch {
        ingredientDao.insertIngredient(ingredient)
    }

    fun update(ingredient: Ingredient) = viewModelScope.launch {
        ingredientDao.updateIngredient(ingredient)
    }

    fun delete(ingredient: Ingredient) = viewModelScope.launch {
        ingredientDao.deleteIngredient(ingredient)
    }
}
