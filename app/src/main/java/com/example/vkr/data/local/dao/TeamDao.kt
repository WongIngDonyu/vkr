package com.example.vkr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.vkr.data.local.model.TeamEntity
import com.example.vkr.data.local.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Insert
    suspend fun insertTeam(team: TeamEntity)

    @Query("SELECT * FROM teams")
    suspend fun getAllTeams(): List<TeamEntity>

    @Query("UPDATE users SET teamId = :teamId WHERE id = :userId")
    suspend fun joinTeam(userId: String, teamId: String)

    @Query("UPDATE users SET teamId = NULL WHERE id = :userId")
    suspend fun leaveTeam(userId: String)

    @Query("SELECT * FROM users WHERE teamId = :teamId")
    suspend fun getUsersByTeam(teamId: String): List<UserEntity>

    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeamById(teamId: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams")
    fun getAllTeamsFlow(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE id = :teamId LIMIT 1")
    suspend fun getTeamByIdOnce(teamId: Int): TeamEntity?

    @Upsert
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Query("DELETE FROM teams")
    suspend fun clearTeams()
}