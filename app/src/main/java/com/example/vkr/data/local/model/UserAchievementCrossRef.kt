package com.example.vkr.data.local.model

import androidx.room.Entity

@Entity(primaryKeys = ["userId", "achievementId"])
data class UserAchievementCrossRef(
    val userId: String,
    val achievementId: Int
)