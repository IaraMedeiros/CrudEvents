package com.example.crudevents.recipeItem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeItemDao {
    @Query("SELECT * FROM recipeitems ORDER BY productId ASC")
    fun getAllRecipeItems(): Flow<List<RecipeItem>>

    @Query("SELECT * FROM recipeitems WHERE productId = :id")
    suspend fun getRecipeItemById(id: Int): RecipeItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeItem(recipeItem: RecipeItem)

    @Update
    suspend fun updateRecipeItem(recipeItem: RecipeItem)

    @Delete
    suspend fun deleteRecipeItem(recipeItem: RecipeItem)
}
