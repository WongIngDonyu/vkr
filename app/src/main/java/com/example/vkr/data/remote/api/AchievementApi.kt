package com.example.vkr.data.remote.api

import com.example.vkr.data.remote.dto.AchievementDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface AchievementApi {
    @GET("achievements/phone/{phone}")
    suspend fun getAchievementsByPhone(@Path("phone") phone: String): List<AchievementDTO>
}