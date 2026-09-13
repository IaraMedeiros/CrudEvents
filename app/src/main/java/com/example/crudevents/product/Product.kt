package com.example.crudevents.product

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var nome: String,

    var preco: Double
)