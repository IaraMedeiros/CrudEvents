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
}
