package com.example.vkr.data.repository

import com.example.vkr.data.model.ActivityDTO
import com.example.vkr.data.remote.FeedApi

class FeedRepository(private val api: FeedApi) {
    suspend fun getRecentActivities(): List<ActivityDTO> {
        return api.getRecentActivities()
    }
}