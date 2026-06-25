package com.example.crudevents.product

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
interface ProductDao {
    /**
     * Retorna todos os productos ordenados por data.
     * O uso de Flow permite que o Room nos avise sempre que os dados mudarem.
     */
    @Query("SELECT * FROM products ORDER BY data ASC")
    fun getAllProducts(): Flow<List<Product>>

    /**
     * Busca um producto específico pelo seu ID.
     */
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?

    /**
     * Insere um novo producto. Se houver conflito de ID, ele substitui (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    /**
     * Atualiza as informações de um producto existente.
     */
    @Update
    suspend fun updateProduct(product: Product)

    /**
     * Remove um producto do banco de dados.
     */
    @Delete
    suspend fun deleteProduct(product: Product)
}