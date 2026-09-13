package com.example.crudevents.ingredients

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var nome: String,
    var quantidadeEstoque: Double,
    var unidade: String
)