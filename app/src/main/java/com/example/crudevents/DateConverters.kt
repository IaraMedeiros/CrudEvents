package com.example.crudevents

import androidx.room.TypeConverter
import java.time.LocalDate

class DateConverters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}
