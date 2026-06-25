package com.example.crudevents.events

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * A "Entidade" representa uma tabela no banco de dados SQLite.
 * Cada instância desta classe será uma linha na tabela "events".
 */
@Entity(tableName = "events")
data class Event(
    /**
     * O PrimaryKey com autoGenerate = true indica que o banco de dados
     * criará um ID único automaticamente para cada novo evento.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,
    val data: LocalDate, // Agora usamos LocalDate graças ao nosso TypeConverter
    val local: String,
    val publicoEstimado: Int
)