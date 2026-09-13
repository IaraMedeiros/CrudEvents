package com.example.crudevents.events

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela que vincula um Produto a um Evento.
 * Permite saber o que levar e em que quantidade.
 */
@Entity(tableName = "event_products")
data class EventProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventId: Int,      // ID do Evento
    val productId: Int,    // ID do Produto
    val quantidade: Double // Quantidade que será levada
)
