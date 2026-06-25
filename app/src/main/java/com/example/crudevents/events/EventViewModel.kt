package com.example.crudevents.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.crudevents.AppDatabase
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * O ViewModel é o "Garçom" do nosso restaurante (MVVM).
 * Ele busca dados do banco (através do DAO) e prepara para a Activity mostrar.
 */
class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventDao = AppDatabase.getDatabase(application).eventDao()

    // Observamos todos os eventos. Como é LiveData, a Activity será avisada
    // automaticamente se qualquer evento for adicionado ou removido.
    val allEvents = eventDao.getAllEvents().asLiveData()

    /**
     * Esta é a forma correta! O ViewModel processa os dados.
     * Sempre que 'allEvents' for atualizado pelo banco, este 'map' roda automaticamente.
     */
    val proximoEvento = allEvents.map { events ->
        val hoje = LocalDate.now()
        events.filter { it.data >= hoje }.firstOrNull()
    }

    /**
     * Insere um novo evento de forma assíncrona.
     */
    fun insert(event: Event) = viewModelScope.launch {
        eventDao.insertEvent(event)
    }

    /**
     * Atualiza um evento existente.
     */
    fun update(event: Event) = viewModelScope.launch {
        eventDao.updateEvent(event)
    }

    /**
     * Remove um evento do banco de dados.
     */
    fun delete(event: Event) = viewModelScope.launch {
        eventDao.deleteEvent(event)
    }
}