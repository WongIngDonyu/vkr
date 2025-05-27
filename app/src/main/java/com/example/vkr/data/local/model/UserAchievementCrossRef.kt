package com.example.vkr.data.local.model

import androidx.room.Entity

@Entity(tableName = "user_achievement_cross_ref", primaryKeys = ["userId", "achievementId"])
data class UserAchievementCrossRef(
    val userId: String,
    val achievementId: String
)