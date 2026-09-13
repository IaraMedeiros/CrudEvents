package com.example.crudevents.events

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val data: LocalDate,
    val local: String,
    val publicoEstimado: Int
)
