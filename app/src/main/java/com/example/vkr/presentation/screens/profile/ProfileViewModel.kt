package com.example.vkr.presentation.screens.profile

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr.data.AppDatabase
import com.example.vkr.data.model.AchievementEntity
import com.example.vkr.data.model.EventEntity
import com.example.vkr.data.model.TeamEntity
import com.example.vkr.data.model.UserEntity
import com.example.vkr.data.repository.UserRepository
import com.example.vkr.data.session.UserSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    var user by mutableStateOf<UserEntity?>(null)
        private set

    var team by mutableStateOf<TeamEntity?>(null)
        private set

    var achievements by mutableStateOf<List<AchievementEntity>>(emptyList())
        private set

    var events by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    var selectedDateFilter by mutableStateOf("Предстоящие")
        private set

    var selectedEvent by mutableStateOf<EventEntity?>(null)
        private set

    init {
        val context = application.applicationContext
        repository = UserRepository(userDao = AppDatabase.getInstance(context).userDao(), teamDao = AppDatabase.getInstance(context).teamDao(), session = UserSessionManager(context))
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val phone = repository.session.userPhone.first()
            if (phone != null) {
                val apiUser = withContext(Dispatchers.IO) {
                    repository.loadUserFromApi(phone)
                }
                if (apiUser != null) {
                    user = apiUser
                    repository.saveUserToDb(apiUser)
                }
            }
            withContext(Dispatchers.IO) {
                val profile = repository.getUserProfile()
                profile?.let {
                    withContext(Dispatchers.Main) {
                        user = it.user
                        achievements = it.achievements
                        events = it.events
                        team = it.team
                    }
                }
            }
        }
    }

    fun selectEvent(event: EventEntity) {
        selectedEvent = event
    }

    fun closeDialog() {
        selectedEvent = null
    }

    fun selectDateFilter(filter: String) {
        selectedDateFilter = filter
    }
}