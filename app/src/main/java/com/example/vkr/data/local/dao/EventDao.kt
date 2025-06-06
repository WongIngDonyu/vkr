package com.example.vkr.data.local.dao

import androidx.room.*
import com.example.vkr.data.local.model.EventEntity
import com.example.vkr.data.local.model.UserWithEvents
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity?

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM user_event_cross_ref WHERE eventId = :eventId")
    suspend fun deleteCrossRefsForEvent(eventId: Int)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserWithEvents(userId: Int): Flow<UserWithEvents?>

    @Upsert
    suspend fun insertEvents(events: List<EventEntity>)
}