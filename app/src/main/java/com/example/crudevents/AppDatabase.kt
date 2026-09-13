package com.example.crudevents

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventDao
import com.example.crudevents.events.EventProduct
import com.example.crudevents.events.EventProductDao
import com.example.crudevents.ingredients.Ingredient
import com.example.crudevents.ingredients.IngredientDao
import com.example.crudevents.product.Product
import com.example.crudevents.product.ProductDao
import com.example.crudevents.recipeItem.RecipeItem
import com.example.crudevents.recipeItem.RecipeItemDao

@Database(
    entities = [Event::class, Product::class, Ingredient::class, RecipeItem::class, EventProduct::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun productDao(): ProductDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeItemDao(): RecipeItemDao
    abstract fun eventProductDao(): EventProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
