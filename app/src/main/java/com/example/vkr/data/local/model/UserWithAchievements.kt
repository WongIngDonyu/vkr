package com.example.vkr.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithAchievements(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = UserAchievementCrossRef::class,
            parentColumn = "userId",
            entityColumn = "achievementId"
        )
    )
    val achievements: List<AchievementEntity>
)