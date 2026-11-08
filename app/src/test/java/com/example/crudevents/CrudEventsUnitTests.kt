package com.example.crudevents

import com.example.crudevents.events.Event
import com.example.crudevents.ingredients.Ingredient
import com.example.crudevents.product.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate

class CrudEventsUnitTests {

    private val converters = DateConverters()

    @Test
    fun `converter deve transformar data em Long corretamente`() {
        val data = LocalDate.of(2024, 12, 25)
        val resultado = converters.dateToEpochDay(data)
        // 1 de Janeiro de 1970 é o dia 0. 25/12/2024 é o dia 20082.
        assertEquals(20082L, resultado)
    }

    @Test
    fun `converter deve transformar Long em data corretamente`() {
        val dias = 20082L
        val resultado = converters.fromEpochDay(dias)
        val dataEsperada = LocalDate.of(2024, 12, 25)
        assertEquals(dataEsperada, resultado)
    }

    @Test
    fun `entidade Event deve armazenar dados corretamente`() {
        val data = LocalDate.now()
        val event = Event(id = 1, nome = "Festa Junina", data = data, local = "Escola", publicoEstimado = 100)
        
        assertEquals("Festa Junina", event.nome)
        assertEquals(data, event.data)
        assertEquals(100, event.publicoEstimado)
    }

    @Test
    fun `entidade Product deve lidar com preco corretamente`() {
        val product = Product(id = 5, nome = "Bolo de Chocolate", preco = 45.50)
        
        assertEquals("Bolo de Chocolate", product.nome)
        assertEquals(45.50, product.preco, 0.01)
    }

    @Test
    fun `entidade Ingredient deve permitir alteracao de estoque`() {
        val ingredient = Ingredient(id = 10, nome = "Acucar", quantidadeEstoque = 5.0, unidade = "kg")
        
        ingredient.quantidadeEstoque = 10.5
        assertEquals(10.5, ingredient.quantidadeEstoque, 0.01)
        assertEquals("kg", ingredient.unidade)
    }

    @Test
    fun `EventCalculator deve calcular ingredientes totais de um evento`() {
        val eventProducts = listOf(
            com.example.crudevents.events.EventProduct(id = 1, eventId = 1, productId = 101, quantidade = 100.0) // 100 Coxinhas
        )
        val recipes = listOf(
            com.example.crudevents.recipeItem.RecipeItem(id = 1, productId = 101, ingredientId = 1, quantidade = 0.05, unidade = "kg") // 0.05kg frango desfiado por coxinha
        )
        val ingredients = listOf(
            Ingredient(id = 1, nome = "Frango Desfiado", quantidadeEstoque = 2.0, unidade = "kg")
        )

        val result = com.example.crudevents.utils.EventCalculator.calculateRequiredIngredients(eventProducts, recipes, ingredients)

        assertEquals(1, result.size)
        assertEquals("Frango Desfiado", result[0].ingredientName)
        assertEquals(5.0, result[0].quantidadeNecessaria, 0.001) // 100 * 0.05 = 5.0 kg
        assertEquals(3.0, result[0].faltaNoEstoque, 0.001) // 5.0 - 2.0 no estoque = 3.0 kg faltando
    }

    @Test
    fun `EventCalculator deve prever demanda por pessoa com base em historico`() {
        val pastEvents = listOf(
            Event(id = 1, nome = "Feira 1", data = LocalDate.now().minusDays(10), local = "Praça", publicoEstimado = 1000)
        )
        val pastProducts = listOf(
            com.example.crudevents.events.EventProduct(id = 1, eventId = 1, productId = 101, quantidade = 200.0) // 200 coxinhas para 1000 pessoas (0.2 coxinha/pessoa)
        )

        val prediction = com.example.crudevents.utils.EventCalculator.predictProductDemandForPublic(
            estimatedPublic = 5000,
            pastEvents = pastEvents,
            pastEventProducts = pastProducts
        )

        assertEquals(1000.0, prediction[101]!!, 0.001) // 0.2 * 5000 pessoas = 1000 coxinhas sugeridas
    }
}
