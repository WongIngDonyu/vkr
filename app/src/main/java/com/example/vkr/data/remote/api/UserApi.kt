package com.example.vkr.data.api

import com.example.vkr.data.remote.dto.UserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserDTO): Response<UserDTO>

    @GET("users/top/week")
    suspend fun getTopUsersWeek(): List<UserDTO>

    @GET("users/top/month")
    suspend fun getTopUsersMonth(): List<UserDTO>

    @GET("users/top/all")
    suspend fun getTopUsersAll(): List<UserDTO>

    @GET("users/eco-hero")
    suspend fun getEcoHeroOfTheWeek(): UserDTO
}