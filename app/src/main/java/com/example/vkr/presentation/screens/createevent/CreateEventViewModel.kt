package com.example.vkr.presentation.screens.createevent

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vkr.data.api.RetrofitInstance
import com.example.vkr.data.local.AppDatabase
import com.example.vkr.data.remote.dto.EventRequestDTO
import com.example.vkr.data.repository.EventRepository
import com.example.vkr.data.session.UserSessionManager
import com.example.vkr.presentation.components.copyImageToInternalStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class CreateEventViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val session = UserSessionManager(context)
    private val repository = EventRepository(api = RetrofitInstance.eventApi, eventDao = AppDatabase.getInstance(context).eventDao(), teamDao = AppDatabase.getInstance(context).teamDao(), userDao = AppDatabase.getInstance(context).userDao())

    var state by mutableStateOf(CreateEventUiState())
        private set

    fun onTitleChange(value: String) {
        state = state.copy(title = value, titleError = false)
    }

    fun onDescriptionChange(value: String) {
        state = state.copy(description = value)
    }

    fun onLocationChange(value: String) {
        state = state.copy(location = value)
    }

    fun onPickDate() {
        state = state.copy(showDatePicker = true)
    }

    fun onDismissDate() {
        state = state.copy(showDatePicker = false)
    }

    fun onDateSelected(millis: Long?) {
        val date = millis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        state = state.copy(selectedDate = date, showDatePicker = false)
    }

    fun onPickTime() {
        state = state.copy(showTimePicker = true)
    }

    fun onDismissTime() {
        state = state.copy(showTimePicker = false)
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        state = state.copy(selectedTime = time, showTimePicker = false)
    }

    fun onImagePicked(uri: Uri?) {
        state = state.copy(imageUri = uri)
    }

    fun onTeamSelected(teamId: String) {
        viewModelScope.launch {
            val team = repository.getTeamById(teamId)
            state = state.copy(
                selectedTeamId = team?.id,
                selectedTeamName = team?.name
            )
        }
    }

    fun onCreateEvent(onSuccess: () -> Unit) {
        val st = state
        val title = st.title.trim()
        if (title.isBlank()) {
            state = st.copy(titleError = true)
            return
        }
        val date = st.selectedDate ?: return
        val time = st.selectedTime ?: return
        val dateTime = LocalDateTime.of(date, time)
        val formattedDateTime = dateTime.toString()
        viewModelScope.launch {
            val phone = session.userPhone.first() ?: return@launch
            val user = repository.getUserByPhone(phone) ?: return@launch
            val imagePath = st.imageUri?.let { copyImageToInternalStorage(context, it) }
            val teamId = st.selectedTeamId ?: user.teamId
            val dto = EventRequestDTO(
                title = title,
                description = st.description,
                locationName = st.location,
                latitude = 55.0,
                longitude = 37.0,
                dateTime = formattedDateTime,
                creatorId = user.id,
                teamId = teamId,
                imageUri = listOfNotNull(imagePath)
            )
            repository.createAndSaveEvent(user, dto)
            onSuccess()
        }
    }
}

data class CreateEventUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val imageUri: Uri? = null,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val titleError: Boolean = false,
    val selectedTeamId: String? = null,
    val selectedTeamName: String? = null
)

class CreateEventViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEventViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}