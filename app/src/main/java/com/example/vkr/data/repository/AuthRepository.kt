package com.example.vkr.data.repository

import com.example.vkr.data.api.AuthApi
import com.example.vkr.data.local.dao.UserDao
import com.example.vkr.data.local.model.UserAchievementCrossRef
import com.example.vkr.data.local.model.UserEntity
import com.example.vkr.data.remote.dto.RegisterDTO
import com.example.vkr.data.remote.dto.UserLoginDTO
import com.example.vkr.data.session.UserSessionManager
import okhttp3.ResponseBody
import retrofit2.Response

class AuthRepository(private val api: AuthApi, private val userDao: UserDao, private val session: UserSessionManager) {

    suspend fun login(phone: String, password: String): Boolean {
        val loginResponse = api.login(UserLoginDTO(phone, password))
        if (loginResponse.isSuccessful) {
            session.saveUser(phone, role = "UNKNOWN")
            return true
        }
        return false
    }

    suspend fun loadAndStoreUserByPhone(phone: String): Boolean {
        val response = api.getUserByPhone(phone)
        if (!response.isSuccessful) return false
        val user = response.body() ?: return false
        val entity = UserEntity(
            id = user.id,
            name = user.name,
            nickname = user.nickname,
            phone = user.phone,
            role = user.role,
            points = user.points,
            eventCount = user.eventCount,
            avatarUri = user.avatarUri,
            teamId = user.teamId
        )
        userDao.insertUser(entity)
//        val achievementRefs = listOf(
//            UserAchievementCrossRef(user.id, 1),
//            UserAchievementCrossRef(user.id, 3)
//        )
//        userDao.insertUserAchievementCrossRefs(achievementRefs)
        session.saveUser(user.phone, user.role)
        return true
    }

    suspend fun register(user: RegisterDTO): Response<ResponseBody> {
        return api.register(user)
    }
}