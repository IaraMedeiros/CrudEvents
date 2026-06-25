package com.example.crudevents

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventDao

/**
 * A classe de Banco de Dados principal.
 * Conecta as Entidades com os DAOs.
 */
@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(DateConverters::class) // Diz ao Room para usar nossos conversores de data
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        /**
         * Padrão Singleton: Garante que só exista UMA instância do banco aberta.
         * Abrir múltiplas instâncias é pesado e pode corromper os dados.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database"
                )
                    .fallbackToDestructiveMigration() // Se a versão mudar, apaga e recria (bom para dev)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
