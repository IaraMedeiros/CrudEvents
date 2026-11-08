package com.example.crudevents.utils

import com.example.crudevents.events.Event
import com.example.crudevents.events.EventProduct
import com.example.crudevents.ingredients.Ingredient
import com.example.crudevents.product.Product
import com.example.crudevents.recipeItem.RecipeItem

data class IngredientRequirement(
    val ingredientId: Int,
    val ingredientName: String,
    val quantidadeNecessaria: Double,
    val unidade: String,
    val quantidadeEstoque: Double,
    val faltaNoEstoque: Double
)

data class EventFinancialSummary(
    val receitaEstimada: Double,
    val custoEstimado: Double,
    val lucroEstimado: Double
)

object EventCalculator {

    /**
     * Algoritmo de cálculo de ingredientes necessários:
     * Para cada produto no evento, multiplica a quantidade do produto pelos ingredientes da receita
     * e consolida os totais necessários para todo o evento.
     */
    fun calculateRequiredIngredients(
        eventProducts: List<EventProduct>,
        allRecipes: List<RecipeItem>,
        allIngredients: List<Ingredient>
    ): List<IngredientRequirement> {
        val ingredientMap = allIngredients.associateBy { it.id }
        val recipesByProduct = allRecipes.groupBy { it.productId }
        val totalsMap = mutableMapOf<Int, Double>()

        for (ep in eventProducts) {
            val recipes = recipesByProduct[ep.productId] ?: emptyList()
            for (recipe in recipes) {
                val currentTotal = totalsMap.getOrDefault(recipe.ingredientId, 0.0)
                totalsMap[recipe.ingredientId] = currentTotal + (recipe.quantidade * ep.quantidade)
            }
        }

        return totalsMap.mapNotNull { (ingredientId, totalNecessario) ->
            val ingredient = ingredientMap[ingredientId]
            val nome = ingredient?.nome ?: "Ingrediente #$ingredientId"
            val unidade = ingredient?.unidade ?: recipeUnitForIngredient(ingredientId, allRecipes)
            val estoque = ingredient?.quantidadeEstoque ?: 0.0
            val falta = (totalNecessario - estoque).coerceAtLeast(0.0)

            IngredientRequirement(
                ingredientId = ingredientId,
                ingredientName = nome,
                quantidadeNecessaria = totalNecessario,
                unidade = unidade,
                quantidadeEstoque = estoque,
                faltaNoEstoque = falta
            )
        }
    }

    private fun recipeUnitForIngredient(ingredientId: Int, recipes: List<RecipeItem>): String {
        return recipes.firstOrNull { it.ingredientId == ingredientId }?.unidade ?: "un"
    }

    /**
     * Calcula a Receita, Custo e Lucro Estimado de um evento.
     */
    fun calculateFinancials(
        eventProducts: List<EventProduct>,
        allProducts: List<Product>
    ): EventFinancialSummary {
        val productMap = allProducts.associateBy { it.id }
        var receitaTotal = 0.0

        for (ep in eventProducts) {
            val prod = productMap[ep.productId]
            if (prod != null) {
                receitaTotal += prod.preco * ep.quantidade
            }
        }

        // Custo estimado pode ser expandido futuramente com preço de compra dos ingredientes
        val custoEstimado = 0.0
        val lucroEstimado = receitaTotal - custoEstimado

        return EventFinancialSummary(
            receitaEstimada = receitaTotal,
            custoEstimado = custoEstimado,
            lucroEstimado = lucroEstimado
        )
    }

    /**
     * Algoritmo de Previsão Inteligente (AI / Estatística per capita):
     * Calcula o consumo médio por pessoa de cada produto nos eventos passados
     * e prevê a quantidade ideal a produzir para um novo público estimado.
     */
    fun predictProductDemandForPublic(
        estimatedPublic: Int,
        pastEvents: List<Event>,
        pastEventProducts: List<EventProduct>
    ): Map<Int, Double> {
        if (estimatedPublic <= 0 || pastEvents.isEmpty()) return emptyMap()

        val eventMap = pastEvents.associateBy { it.id }
        val productConsumptionRates = mutableMapOf<Int, MutableList<Double>>()

        for (ep in pastEventProducts) {
            val event = eventMap[ep.eventId]
            if (event != null && event.publicoEstimado > 0) {
                val ratePerPerson = ep.quantidade / event.publicoEstimado.toDouble()
                productConsumptionRates.getOrPut(ep.productId) { mutableListOf() }.add(ratePerPerson)
            }
        }

        val predictionMap = mutableMapOf<Int, Double>()
        for ((productId, rates) in productConsumptionRates) {
            val averageRatePerPerson = rates.average()
            predictionMap[productId] = averageRatePerPerson * estimatedPublic
        }

        return predictionMap
    }
}
