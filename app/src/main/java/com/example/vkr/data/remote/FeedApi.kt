package com.example.vkr.data.remote

import com.example.vkr.data.model.ActivityDTO
import retrofit2.http.GET

interface FeedApi {
    @GET("/api/feed/feed")
    suspend fun getRecentActivities(): List<ActivityDTO>
}