package com.example.vkr.data.remote.dto

data class UserDTO(
    val id: String,
    val name: String,
    val nickname: String,
    val phone: String,
    val role: String,
    val points: Int,
    val eventCount: Int,
    val avatarUri: String?,
    val teamId: String?
)