package com.example.vkr.data.api

import com.example.vkr.data.remote.dto.TeamDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface  TeamApi {
    @GET("/teams")
    suspend fun getAllTeams(): Response<List<TeamDTO>>

    @PUT("teams/{teamId}/join/{userId}")
    suspend fun joinTeam(@Path("teamId") teamId: String, @Path("userId") userId: String): Response<Void>

    @PUT("teams/{teamId}/leave/{userId}")
    suspend fun leaveTeam(@Path("teamId") teamId: String, @Path("userId") userId: String): Response<Void>

    @GET("teams/top/all")
    suspend fun getTopTeamsAll(): List<TeamDTO>

    @GET("teams/top/week")
    suspend fun getTopTeamsWeek(): List<TeamDTO>

    @GET("teams/top/month")
    suspend fun getTopTeamsMonth(): List<TeamDTO>
}