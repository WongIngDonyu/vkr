package com.example.vkr.data.api

import com.example.vkr.data.remote.dto.RegisterDTO
import com.example.vkr.data.remote.dto.UserDTO
import com.example.vkr.data.remote.dto.UserLoginDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("/auth/register")
    suspend fun register(@Body user: RegisterDTO): Response<ResponseBody>

    @POST("/auth/login")
    suspend fun login(@Body credentials: UserLoginDTO): Response<ResponseBody>

    @GET("/users/phone/{phone}")
    suspend fun getUserByPhone(@Path("phone") phone: String): Response<UserDTO>
}