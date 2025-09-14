package com.app.eventplannerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.app.eventplannerapp.data.db.EventDatabase
import com.app.eventplannerapp.data.entity.EventEntity
import com.app.eventplannerapp.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository =
        EventRepository(EventDatabase.Companion.getInstance(application).eventDao())

    private val _selectedDayMillis = MutableLiveData<Long>(startOfToday())
    val selectedDayMillis: LiveData<Long> = _selectedDayMillis

    val eventsForSelectedDay: LiveData<List<EventEntity>> = _selectedDayMillis.switchMap { dayStart ->
        val dayEnd = endOfDay(dayStart)
        repository.getEventsForDay(dayStart, dayEnd)
    }

    val upcomingEvents: LiveData<List<EventEntity>> = repository.getUpcoming(System.currentTimeMillis(), 10)
    val allEvents: LiveData<List<EventEntity>> = repository.getAllEvents()

    fun setSelectedDate(millis: Long) {
        _selectedDayMillis.value = startOfDay(millis)
    }

    fun addEvent(title: String, description: String, eventTimeMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEvent(
                EventEntity(
                    title = title,
                    description = description,
                    startTimeMillis = eventTimeMillis
                )
            )
        }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateEvent(event) }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteEvent(event) }
    }

    private fun startOfToday(): Long = startOfDay(System.currentTimeMillis())

    private fun startOfDay(timeMillis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis; set(Calendar.HOUR_OF_DAY, 0); set(
            Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        return cal.timeInMillis
    }

    private fun endOfDay(dayStartMillis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = dayStartMillis; set(Calendar.HOUR_OF_DAY, 23); set(
            Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999) }
        return cal.timeInMillis + 1
    }
}