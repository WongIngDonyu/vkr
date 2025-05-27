package com.example.vkr.data.repository

import com.example.vkr.data.api.FeedApi
import com.example.vkr.data.remote.dto.ActivityDTO

class FeedRepository(private val api: FeedApi) {
    suspend fun getRecentActivities(): List<ActivityDTO> {
        return api.getRecentActivities()
    }
}