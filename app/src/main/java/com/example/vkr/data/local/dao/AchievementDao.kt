package com.example.vkr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vkr.data.local.model.AchievementEntity
import com.example.vkr.data.local.model.UserAchievementCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAchievementCrossRef(crossRef: UserAchievementCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAchievementCrossRefs(crossRefs: List<UserAchievementCrossRef>)
}