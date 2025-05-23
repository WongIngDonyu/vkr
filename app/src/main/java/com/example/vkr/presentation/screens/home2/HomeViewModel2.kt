package com.example.vkr.presentation.screens.home2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vkr.data.model.TeamDTO
import com.example.vkr.data.model.UserDTO
import com.example.vkr.data.model.UserEntity
import com.example.vkr.data.repository.FeedRepository
import com.example.vkr.data.repository.TeamRepository
import com.example.vkr.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ActivityItem(val user: String, val description: String, val time: String, val avatarUrl: String)
data class EventItem(val id: String, val title: String, val participants: Int)

enum class LeaderboardTab { USERS, TEAMS }
enum class LeaderboardPeriod { WEEK, MONTH, ALL_TIME }

class HomeViewModel2(private val userRepository: UserRepository, private val teamRepository: TeamRepository, private val feedRepository: FeedRepository) : ViewModel() {

    private val _userName = MutableStateFlow("")

    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val events: StateFlow<List<EventItem>> = _events

    private val _selectedTab = MutableStateFlow(LeaderboardTab.USERS)
    val selectedTab: StateFlow<LeaderboardTab> = _selectedTab

    private val _selectedPeriod = MutableStateFlow(LeaderboardPeriod.WEEK)
    val selectedPeriod: StateFlow<LeaderboardPeriod> = _selectedPeriod

    private val _userLeaderboard = MutableStateFlow<List<UserDTO>>(emptyList())
    val userLeaderboard: StateFlow<List<UserDTO>> = _userLeaderboard

    private val _ecoHero = MutableStateFlow<UserDTO?>(null)
    val ecoHero: StateFlow<UserDTO?> = _ecoHero

    private val _teamLeaderboard = MutableStateFlow<List<TeamDTO>>(emptyList())
    val teamLeaderboard: StateFlow<List<TeamDTO>> = _teamLeaderboard

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
    val activities: StateFlow<List<ActivityItem>> = _activities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserData()
        loadLeaderboard()
        loadEcoHero()
    }

    fun selectTab(tab: LeaderboardTab) {
        _selectedTab.value = tab
        loadLeaderboard()
    }

    fun selectPeriod(period: LeaderboardPeriod) {
        _selectedPeriod.value = period
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (_selectedTab.value == LeaderboardTab.USERS) {
                    val users = userRepository.getTopUsers(_selectedPeriod.value)
                    _userLeaderboard.value = users
                } else {
                    val teams = teamRepository.getTopTeams(_selectedPeriod.value)
                    _teamLeaderboard.value = teams
                }
            } catch (e: Exception) {
                _userLeaderboard.value = emptyList()
                _teamLeaderboard.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                teamRepository.syncTeamsFromRemote()
                val phone = userRepository.session.userPhone.firstOrNull()
                if (phone != null) {
                    val user = userRepository.loadUserFromApi(phone)
                    user?.let {
                        _user.value = it
                        _userName.value = it.name
                        loadFeed()
                    }
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadEcoHero() {
        viewModelScope.launch {
            try {
                val hero = userRepository.getEcoHero()
                _ecoHero.value = hero
            } catch (e: Exception) {
                _ecoHero.value = null
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            try {
                val activityDtos = feedRepository.getRecentActivities()
                _activities.value = activityDtos.map {
                    ActivityItem(
                        user = "",
                        description = it.message,
                        time = it.timestamp,
                        avatarUrl = ""
                    )
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel2", "Failed to load feed", e)
            }
        }
    }
}

class HomeViewModelFactory(private val userRepository: UserRepository, private val teamRepository: TeamRepository, private val feedRepository: FeedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel2::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel2(userRepository, teamRepository, feedRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}