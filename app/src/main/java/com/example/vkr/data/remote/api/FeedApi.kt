package com.example.vkr.data.api

import com.example.vkr.data.remote.dto.ActivityDTO
import retrofit2.http.GET

interface FeedApi {
    @GET("/api/feed/feed")
    suspend fun getRecentActivities(): List<ActivityDTO>
}