package com.example.vkr.data.remote.dto

data class RegisterDTO(
    val name: String,
    val nickname: String,
    val phone: String,
    val password: String,
    val role: String = "USER"
)