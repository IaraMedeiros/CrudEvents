package com.example.crudevents.events

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventProductDao {
    /**
     * Retorna todos os produtos vinculados a um evento específico.
     */
    @Query("SELECT * FROM event_products WHERE eventId = :eventId")
    fun getProductsByEvent(eventId: Int): Flow<List<EventProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventProduct(eventProduct: EventProduct)

    @Update
    suspend fun updateEventProduct(eventProduct: EventProduct)

    @Delete
    suspend fun deleteEventProduct(eventProduct: EventProduct)

    /**
     * Remove todos os itens vinculados a um evento (usado antes de atualizar a lista).
     */
    @Query("DELETE FROM event_products WHERE eventId = :eventId")
    suspend fun deleteAllByEvent(eventId: Int)
}
