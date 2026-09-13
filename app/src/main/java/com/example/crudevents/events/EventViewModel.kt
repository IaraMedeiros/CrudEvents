package com.example.crudevents.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.crudevents.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(application: Application) : AndroidViewModel(application) {
    
    private val db = AppDatabase.getDatabase(application)
    private val eventDao = db.eventDao()
    private val eventProductDao = db.eventProductDao()
    
    val allEvents = eventDao.getAllEvents().asLiveData()

    val upcomingEvents = allEvents.map { events ->
        val hoje = LocalDate.now()
        events.filter { it.data >= hoje }
    }

    val pastEvents = allEvents.map { events ->
        val hoje = LocalDate.now()
        events.filter { it.data < hoje }.reversed() // Inverte para os mais recentes aparecerem primeiro
    }

    val proximoEvento = upcomingEvents.map { it.firstOrNull() }

    fun saveEventWithProducts(event: Event, items: List<EventProduct>) = viewModelScope.launch {
        val generatedId = eventDao.insertEvent(event).toInt()
        val finalEventId = if (event.id != 0) event.id else generatedId
        eventProductDao.deleteAllByEvent(finalEventId)
        items.forEach { item ->
            eventProductDao.insertEventProduct(item.copy(eventId = finalEventId))
        }
    }

    fun getProductsForEvent(eventId: Int) = eventProductDao.getProductsByEvent(eventId).asLiveData()
    fun insert(event: Event) = viewModelScope.launch { eventDao.insertEvent(event) }
    fun update(event: Event) = viewModelScope.launch { eventDao.updateEvent(event) }
    fun delete(event: Event) = viewModelScope.launch {
        eventDao.deleteEvent(event)
        eventProductDao.deleteAllByEvent(event.id)
    }
}
