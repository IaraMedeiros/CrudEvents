package com.example.crudevents.recipeItem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipeitems")
data class RecipeItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val ingredientId: Int,
    val quantidade: Double,
    val unidade: String = "g" // Adicionado para salvar a medida
)
