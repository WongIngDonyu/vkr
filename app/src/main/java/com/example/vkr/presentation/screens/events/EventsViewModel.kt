package com.example.vkr.presentation.screens.events

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkr.data.api.RetrofitInstance
import com.example.vkr.data.local.AppDatabase
import com.example.vkr.data.local.model.EventEntity
import com.example.vkr.data.repository.EventRepository
import com.example.vkr.data.session.UserSessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val session = UserSessionManager(context)

    private val repository = EventRepository(api = RetrofitInstance.eventApi, eventDao = AppDatabase.getInstance(context).eventDao(), teamDao = AppDatabase.getInstance(context).teamDao(), userDao = AppDatabase.getInstance(context).userDao())

    private var userId: String? = null

    val currentUserId: String?
        get() = userId

    var allEvents by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    var otherEvents by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    var selectedEvent by mutableStateOf<EventEntity?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf("Все")
        private set

    var isOrganizer by mutableStateOf(false)
        private set

    var joinedEvents by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    var organizedEvents by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    var filteredEvents by mutableStateOf<List<EventEntity>>(emptyList())
        private set

    private var userTeamId: String? = null
    val currentUserTeamId: String? get() = userTeamId

    init {
        viewModelScope.launch {
            val phone = session.userPhone.firstOrNull()
            val user = phone?.let { repository.getUserByPhone(it) }
            userId = user?.id
            userTeamId = user?.teamId
            isOrganizer = user?.role == "ORGANIZER"
            repository.fetchAndSaveEvents()
            repository.getAllEventsFlow().collect { events ->
                allEvents = events
                loadUserEvents()
            }
        }
    }

    fun onSearchChanged(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun onFilterSelected(filter: String) {
        selectedFilter = filter
        applyFilters()
    }

    fun onEventClick(event: EventEntity) {
        selectedEvent = event
    }

    fun onDialogDismiss() {
        selectedEvent = null
    }

    fun joinEvent(eventId: String, onSnackbar: (String) -> Unit) {
        val uid = userId ?: return
        val event = allEvents.find { it.id == eventId } ?: return

        if (event.creatorId == uid || joinedEvents.any { it.id == eventId }) return

        viewModelScope.launch {
            try {
                val success = repository.joinEvent(uid, eventId)
                if (success) {
                    joinedEvents = joinedEvents + event
                    otherEvents = otherEvents - event
                    onSnackbar("Вы присоединились к мероприятию!")
                    applyFilters()
                } else {
                    onSnackbar("Не удалось присоединиться")
                }
            } catch (e: Exception) {
                onSnackbar("Ошибка подключения: ${e.localizedMessage}")
            }
        }
    }

    fun leaveEvent(eventId: String, onSnackbar: (String) -> Unit) {
        val uid = userId ?: return
        val event = allEvents.find { it.id == eventId } ?: return
        if (event.creatorId == uid || event.isFinished || event.completed || event.verified) {
            return
        }
        viewModelScope.launch {
            try {
                val success = repository.leaveEvent(uid, eventId)
                if (success) {
                    joinedEvents = joinedEvents - event
                    otherEvents = otherEvents + event
                    onSnackbar("Вы покинули мероприятие!")
                    applyFilters()
                } else {
                    onSnackbar("Не удалось покинуть мероприятие")
                }
            } catch (e: Exception) {
                onSnackbar("Ошибка подключения: ${e.localizedMessage}")
            }
        }
    }
    fun loadUserEvents() {
        viewModelScope.launch {
            val phone = session.userPhone.firstOrNull()
            val user = phone?.let { repository.getUserByPhone(it) }
            userId = user?.id
            userTeamId = user?.teamId
            isOrganizer = user?.role == "ORGANIZER"
            val uid = userId ?: return@launch
            val userEvents = repository.getJoinedEventsForUser(uid)
            joinedEvents = userEvents.filter { it.creatorId != uid }
            organizedEvents = userEvents.filter { it.creatorId == uid }

            applyFilters()
        }
    }
    private fun applyFilters() {
        val now = LocalDate.now()
        val filtered = allEvents.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
        val filteredByTime = when (selectedFilter) {
            "Сегодня" -> filtered.filter {
                LocalDateTime.parse(it.dateTime)?.toLocalDate() == now
            }
            "На неделе" -> filtered.filter {
                val date = LocalDateTime.parse(it.dateTime)?.toLocalDate()
                date != null && date in now..now.plusDays(6)
            }
            "В этом месяце" -> filtered.filter {
                LocalDateTime.parse(it.dateTime)?.month == now.month
            }
            else -> filtered
        }
        filteredEvents = filteredByTime
        viewModelScope.launch {
            val uid = userId ?: return@launch
            val userEvents = repository.getJoinedEventsForUser(uid)
            joinedEvents = filteredByTime.filter { event ->
                userEvents.any { it.id == event.id } && event.creatorId != uid
            }
            organizedEvents = filteredByTime.filter { it.creatorId == uid }
            otherEvents = filteredByTime.filter {
                it.id !in joinedEvents.map { it.id } && it.creatorId != uid
            }
        }
    }
}