package com.example.vkr.data.repository

import android.content.Context
import android.net.Uri
import com.example.vkr.data.api.RetrofitInstance
import com.example.vkr.data.local.dao.AchievementDao
import com.example.vkr.data.local.dao.TeamDao
import com.example.vkr.data.local.dao.UserDao
import com.example.vkr.data.local.model.AchievementEntity
import com.example.vkr.data.local.model.EventEntity
import com.example.vkr.data.local.model.TeamEntity
import com.example.vkr.data.local.model.UserAchievementCrossRef
import com.example.vkr.data.local.model.UserEntity
import com.example.vkr.data.remote.dto.AchievementDTO
import com.example.vkr.data.remote.dto.UserDTO
import com.example.vkr.data.session.UserSessionManager
import com.example.vkr.presentation.screens.home2.LeaderboardPeriod
import com.example.vkr.presentation.components.copyImageToInternalStorage
import kotlinx.coroutines.flow.firstOrNull

class UserRepository(private val userDao: UserDao, private val teamDao: TeamDao, val session: UserSessionManager, private val achievementDao: AchievementDao) {

    suspend fun getUserProfile(): UserProfileData? {
        val phone = session.userPhone.firstOrNull() ?: return null
        val user = userDao.getUserByPhone(phone) ?: return null

        val achievements = userDao.getUserWithAchievements(user.id)
            .firstOrNull()?.achievements.orEmpty()

        val events = userDao.getUserWithEvents(user.id)
            .firstOrNull()?.events.orEmpty()

        val team = user.teamId?.let { teamId ->
            teamDao.getAllTeams().firstOrNull { it.id == teamId }
        }
        return UserProfileData(user = user, team = team, achievements = achievements, events = events)
    }

    suspend fun loadUserFromApi(phone: String): UserEntity? {
        return try {
            val response = RetrofitInstance.api.getUserByPhone(phone)
            if (response.isSuccessful) {
                response.body()?.toEntity()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: UserEntity, fullName: String, nickname: String, newAvatarUri: Uri?, context: Context): Boolean {
        val avatarPath = newAvatarUri?.let { copyImageToInternalStorage(context, it) } ?: user.avatarUri
        val updatedDto = UserDTO(
            id = user.id,
            name = fullName,
            nickname = nickname,
            phone = user.phone,
            role = user.role,
            points = user.points,
            eventCount = user.eventCount,
            avatarUri = avatarPath,
            teamId = user.teamId
        )
        return try {
            val response = RetrofitInstance.userApi.updateUser(user.id, updatedDto)
            if (response.isSuccessful) {
                response.body()?.let { updated ->
                    userDao.updateUser(updated.toEntity())
                    session.saveUser(updated.phone, updated.role)
                    return true
                }
                false
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTopUsers(period: LeaderboardPeriod): List<UserDTO> {
        return try {
            when (period) {
                LeaderboardPeriod.WEEK -> RetrofitInstance.userApi.getTopUsersWeek()
                LeaderboardPeriod.MONTH -> RetrofitInstance.userApi.getTopUsersMonth()
                LeaderboardPeriod.ALL_TIME -> RetrofitInstance.userApi.getTopUsersAll()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveUserToDb(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun getEcoHero(): UserDTO? {
        return try {
            RetrofitInstance.userApi.getEcoHeroOfTheWeek()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchAndSaveAchievements(phone: String): List<AchievementEntity>? {
        return try {
            val apiAchievements = RetrofitInstance.achievementApi.getAchievementsByPhone(phone)
            val user = userDao.getUserByPhone(phone) ?: return null
            val entities = apiAchievements.map { it.toEntity() }
            entities.forEach { entity ->
                achievementDao.insertAchievement(entity)
                achievementDao.insertUserAchievementCrossRef(
                    UserAchievementCrossRef(userId = user.id, achievementId = entity.id)
                )
            }
            entities
        } catch (e: Exception) {
            println("Ошибка сохранения: ${e.localizedMessage}")
            null
        }
    }
}

data class UserProfileData(
    val user: UserEntity,
    val team: TeamEntity?,
    val achievements: List<AchievementEntity>,
    val events: List<EventEntity>
)

fun AchievementDTO.toEntity(): AchievementEntity {
    return AchievementEntity(
        id = this.id,
        title = this.title,
        description = this.description.orEmpty(),
        imageResId = this.imageResId
    )
}