package com.app.eventplannerapp.data.repository

import androidx.lifecycle.LiveData
import com.app.eventplannerapp.data.dao.EventDao
import com.app.eventplannerapp.data.entity.EventEntity

class EventRepository(private val eventDao: EventDao) {
    suspend fun addEvent(event: EventEntity): Long = eventDao.insert(event)
    suspend fun updateEvent(event: EventEntity) = eventDao.update(event)
    suspend fun deleteEvent(event: EventEntity) = eventDao.delete(event)

    fun getEventsForDay(startOfDayMillis: Long, endOfDayMillis: Long): LiveData<List<EventEntity>> =
        eventDao.eventsBetween(startOfDayMillis, endOfDayMillis)

    fun getUpcoming(nowMillis: Long, limit: Int): LiveData<List<EventEntity>> =
        eventDao.upcoming(nowMillis, limit)

    fun getAllEvents(): LiveData<List<EventEntity>> = eventDao.allEvents()
}

