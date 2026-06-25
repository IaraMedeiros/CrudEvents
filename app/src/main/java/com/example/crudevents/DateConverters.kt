package com.example.crudevents

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * O Room não sabe como salvar objetos complexos como LocalDate nativamente.
 * Esta classe ensina o Room a converter LocalDate para um número (Long) e vice-versa.
 */
class DateConverters {
    /**
     * Quando LERMOS do banco: Converte o número Long de volta para LocalDate.
     */
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    /**
     * Quando SALVARMOS no banco: Converte o LocalDate para um número Long.
     */
    @TypeConverter
    fun dateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}
