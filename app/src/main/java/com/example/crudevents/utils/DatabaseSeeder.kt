package com.example.crudevents.utils

import android.content.Context
import com.example.crudevents.AppDatabase
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventProduct
import com.example.crudevents.ingredients.Ingredient
import com.example.crudevents.product.Product
import com.example.crudevents.recipeItem.RecipeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

object DatabaseSeeder {

    /**
     * Popula o banco de dados com uma massa de teste realista para um Food Truck (Churros & Tapioca).
     */
    suspend fun seedDatabase(context: Context, forceRecreate: Boolean = false) = withContext(Dispatchers.IO) {
        val db = AppDatabase.getDatabase(context)
        val eventDao = db.eventDao()
        val productDao = db.productDao()
        val ingredientDao = db.ingredientDao()
        val recipeItemDao = db.recipeItemDao()
        val eventProductDao = db.eventProductDao()

        // Limpa massa anterior se forceRecreate = true
        if (forceRecreate) {
            db.clearAllTables()
        }

        // 1. Ingredientes
        val ingFarinhaTrigo = Ingredient(id = 1, nome = "Farinha de Trigo", quantidadeEstoque = 25.0, unidade = "kg")
        val ingGomaTapioca = Ingredient(id = 2, nome = "Goma de Tapioca", quantidadeEstoque = 30.0, unidade = "kg")
        val ingDoceDeLeite = Ingredient(id = 3, nome = "Doce de Leite", quantidadeEstoque = 15.0, unidade = "kg")
        val ingNutella = Ingredient(id = 4, nome = "Nutella / Creme de Avelã", quantidadeEstoque = 10.0, unidade = "kg")
        val ingQueijoCoalho = Ingredient(id = 5, nome = "Queijo Coalho", quantidadeEstoque = 8.0, unidade = "kg")
        val ingPresunto = Ingredient(id = 6, nome = "Presunto Fatiado", quantidadeEstoque = 5.0, unidade = "kg")
        val ingFrangoDesfiado = Ingredient(id = 7, nome = "Frango Desfiado", quantidadeEstoque = 12.0, unidade = "kg")
        val ingCatupiry = Ingredient(id = 8, nome = "Requeijão / Catupiry", quantidadeEstoque = 6.0, unidade = "kg")
        val ingCarneSeca = Ingredient(id = 9, nome = "Carne Seca Desfiada", quantidadeEstoque = 7.0, unidade = "kg")
        val ingAcucarCanela = Ingredient(id = 10, nome = "Açúcar e Canela", quantidadeEstoque = 5.0, unidade = "kg")
        val ingOleoFritura = Ingredient(id = 11, nome = "Óleo para Fritura", quantidadeEstoque = 20.0, unidade = "L")
        val ingCocoRalado = Ingredient(id = 12, nome = "Coco Ralado", quantidadeEstoque = 3.0, unidade = "kg")
        val ingLeiteCondensado = Ingredient(id = 13, nome = "Leite Condensado", quantidadeEstoque = 8.0, unidade = "kg")

        val ingredients = listOf(
            ingFarinhaTrigo, ingGomaTapioca, ingDoceDeLeite, ingNutella, ingQueijoCoalho,
            ingPresunto, ingFrangoDesfiado, ingCatupiry, ingCarneSeca, ingAcucarCanela,
            ingOleoFritura, ingCocoRalado, ingLeiteCondensado
        )

        ingredients.forEach { ingredientDao.insertIngredient(it) }

        // 2. Produtos
        val prodChurrosDDL = Product(id = 1, nome = "Churros de Doce de Leite", preco = 12.00)
        val prodChurrosNutella = Product(id = 2, nome = "Churros de Nutella", preco = 14.00)
        val prodChurrosEspecial = Product(id = 3, nome = "Churros Especial (DDL + Coco)", preco = 15.00)
        val prodTapiocaFrango = Product(id = 4, nome = "Tapioca de Frango c/ Catupiry", preco = 18.00)
        val prodTapiocaCarneSeca = Product(id = 5, nome = "Tapioca de Carne Seca c/ Queijo", preco = 22.00)
        val prodTapiocaPresuntoQueijo = Product(id = 6, nome = "Tapioca de Presunto e Queijo", preco = 16.00)
        val prodTapiocaPrestigio = Product(id = 7, nome = "Tapioca Prestígio (Doce)", preco = 15.00)

        val products = listOf(
            prodChurrosDDL, prodChurrosNutella, prodChurrosEspecial,
            prodTapiocaFrango, prodTapiocaCarneSeca, prodTapiocaPresuntoQueijo, prodTapiocaPrestigio
        )

        products.forEach { productDao.insertProduct(it) }

        // 3. Fichas Técnicas (Receitas)
        val recipes = listOf(
            // Churros DDL (id 1)
            RecipeItem(productId = 1, ingredientId = 1, quantidade = 0.06, unidade = "kg"),  // Farinha
            RecipeItem(productId = 1, ingredientId = 11, quantidade = 0.05, unidade = "L"),  // Óleo
            RecipeItem(productId = 1, ingredientId = 10, quantidade = 0.01, unidade = "kg"), // Açúcar/Canela
            RecipeItem(productId = 1, ingredientId = 3, quantidade = 0.05, unidade = "kg"),  // DDL

            // Churros Nutella (id 2)
            RecipeItem(productId = 2, ingredientId = 1, quantidade = 0.06, unidade = "kg"),
            RecipeItem(productId = 2, ingredientId = 11, quantidade = 0.05, unidade = "L"),
            RecipeItem(productId = 2, ingredientId = 10, quantidade = 0.01, unidade = "kg"),
            RecipeItem(productId = 2, ingredientId = 4, quantidade = 0.05, unidade = "kg"),  // Nutella

            // Churros Especial (id 3)
            RecipeItem(productId = 3, ingredientId = 1, quantidade = 0.06, unidade = "kg"),
            RecipeItem(productId = 3, ingredientId = 11, quantidade = 0.05, unidade = "L"),
            RecipeItem(productId = 3, ingredientId = 3, quantidade = 0.05, unidade = "kg"),  // DDL
            RecipeItem(productId = 3, ingredientId = 12, quantidade = 0.015, unidade = "kg"),// Coco

            // Tapioca Frango c/ Catupiry (id 4)
            RecipeItem(productId = 4, ingredientId = 2, quantidade = 0.12, unidade = "kg"),  // Goma Tapioca
            RecipeItem(productId = 4, ingredientId = 7, quantidade = 0.10, unidade = "kg"),  // Frango
            RecipeItem(productId = 4, ingredientId = 8, quantidade = 0.04, unidade = "kg"),  // Catupiry

            // Tapioca Carne Seca c/ Queijo (id 5)
            RecipeItem(productId = 5, ingredientId = 2, quantidade = 0.12, unidade = "kg"),
            RecipeItem(productId = 5, ingredientId = 9, quantidade = 0.10, unidade = "kg"),  // Carne Seca
            RecipeItem(productId = 5, ingredientId = 5, quantidade = 0.05, unidade = "kg"),  // Queijo Coalho

            // Tapioca Presunto e Queijo (id 6)
            RecipeItem(productId = 6, ingredientId = 2, quantidade = 0.12, unidade = "kg"),
            RecipeItem(productId = 6, ingredientId = 6, quantidade = 0.05, unidade = "kg"),  // Presunto
            RecipeItem(productId = 6, ingredientId = 5, quantidade = 0.05, unidade = "kg"),  // Queijo Coalho

            // Tapioca Prestígio (id 7)
            RecipeItem(productId = 7, ingredientId = 2, quantidade = 0.12, unidade = "kg"),
            RecipeItem(productId = 7, ingredientId = 13, quantidade = 0.05, unidade = "kg"), // Leite Condensado
            RecipeItem(productId = 7, ingredientId = 12, quantidade = 0.02, unidade = "kg")  // Coco
        )

        recipes.forEach { recipeItemDao.insertRecipeItem(it) }

        // 4. Eventos (Históricos e Futuros)
        val today = LocalDate.now()

        val eventPassado1 = Event(id = 1, nome = "Feira Noturna Gastronômica", data = today.minusDays(15), local = "Praça Central", publicoEstimado = 2500)
        val eventPassado2 = Event(id = 2, nome = "Festival de Food Trucks", data = today.minusDays(40), local = "Parque da Cidade", publicoEstimado = 6000)
        val eventFuturo1 = Event(id = 3, nome = "Mega Feira Universitária", data = today.plusDays(5), local = "Campus Universitario", publicoEstimado = 4000)
        val eventFuturo2 = Event(id = 4, nome = "Arraiá do Food Truck", data = today.plusDays(25), local = "Espaço Sul", publicoEstimado = 3500)

        val events = listOf(eventPassado1, eventPassado2, eventFuturo1, eventFuturo2)
        events.forEach { eventDao.insertEvent(it) }

        // 5. Produtos dos Eventos (EventProducts)
        val eventProducts = listOf(
            // Evento Passado 1 (2500 pessoas)
            EventProduct(eventId = 1, productId = 1, quantidade = 250.0), // Churros DDL
            EventProduct(eventId = 1, productId = 2, quantidade = 180.0), // Churros Nutella
            EventProduct(eventId = 1, productId = 4, quantidade = 150.0), // Tapioca Frango
            EventProduct(eventId = 1, productId = 5, quantidade = 120.0), // Tapioca Carne Seca

            // Evento Passado 2 (6000 pessoas)
            EventProduct(eventId = 2, productId = 1, quantidade = 600.0),
            EventProduct(eventId = 2, productId = 2, quantidade = 450.0),
            EventProduct(eventId = 2, productId = 4, quantidade = 350.0),
            EventProduct(eventId = 2, productId = 5, quantidade = 280.0),
            EventProduct(eventId = 2, productId = 6, quantidade = 200.0), // Tapioca Presunto/Queijo

            // Evento Futuro 1 (4000 pessoas)
            EventProduct(eventId = 3, productId = 1, quantidade = 400.0),
            EventProduct(eventId = 3, productId = 2, quantidade = 300.0),
            EventProduct(eventId = 3, productId = 4, quantidade = 250.0),
            EventProduct(eventId = 3, productId = 5, quantidade = 200.0),
            EventProduct(eventId = 3, productId = 7, quantidade = 150.0), // Tapioca Prestígio

            // Evento Futuro 2 (3500 pessoas)
            EventProduct(eventId = 4, productId = 1, quantidade = 350.0),
            EventProduct(eventId = 4, productId = 2, quantidade = 250.0),
            EventProduct(eventId = 4, productId = 4, quantidade = 220.0),
            EventProduct(eventId = 4, productId = 5, quantidade = 180.0)
        )

        eventProducts.forEach { eventProductDao.insertEventProduct(it) }
    }
}
