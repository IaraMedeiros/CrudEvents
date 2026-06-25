package com.example.crudevents.events

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) é onde definimos como acessar os dados.
 * É como um "contrato" com o banco de dados.
 */
@Dao
interface EventDao {
    /**
     * Retorna todos os eventos ordenados por data.
     * O uso de Flow permite que o Room nos avise sempre que os dados mudarem.
     */
    @Query("SELECT * FROM events ORDER BY data ASC")
    fun getAllEvents(): Flow<List<Event>>

    /**
     * Busca um evento específico pelo seu ID.
     */
    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): Event?

    /**
     * Insere um novo evento. Se houver conflito de ID, ele substitui (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    /**
     * Atualiza as informações de um evento existente.
     */
    @Update
    suspend fun updateEvent(event: Event)

    /**
     * Remove um evento do banco de dados.
     */
    @Delete
    suspend fun deleteEvent(event: Event)
}