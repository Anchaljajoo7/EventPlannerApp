package com.app.eventplannerapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.eventplannerapp.data.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("SELECT * FROM events WHERE startTimeMillis >= :startInclusive AND startTimeMillis < :endExclusive ORDER BY startTimeMillis ASC")
    fun eventsBetween(startInclusive: Long, endExclusive: Long): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startTimeMillis >= :now ORDER BY startTimeMillis ASC LIMIT :limit")
    fun upcoming(now: Long, limit: Int): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY startTimeMillis ASC")
    fun allEvents(): LiveData<List<EventEntity>>
}

