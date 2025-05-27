package com.example.vkr.data.remote.dto

data class EventRequestDTO(
    val title: String,
    val description: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val dateTime: String,
    val creatorId: String,
    val teamId: String?,
    val imageUri: List<String> = emptyList(),
    val isFinished: Boolean = false,
    val verified: Boolean = false,
    val completed: Boolean = false
)