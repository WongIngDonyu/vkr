package com.example.vkr.presentation.screens.events

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vkr.R
import com.example.vkr.data.local.model.EventEntity
import com.example.vkr.presentation.components.DateTimeUtils
import com.example.vkr.presentation.components.EventCardItem
import com.example.vkr.presentation.components.MyEventItem

import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun EventsScreen(modifier: Modifier = Modifier, snackbarHostState: SnackbarHostState, navController: NavController, viewModel: EventsViewModel = viewModel()) {
    val events = viewModel.filteredEvents
    val joinedEvents = viewModel.joinedEvents
    val organizedEvents = viewModel.organizedEvents
    val selectedEvent = viewModel.selectedEvent
    val searchQuery = viewModel.searchQuery
    val selectedFilter = viewModel.selectedFilter
    val isOrganizer = viewModel.isOrganizer
    val scope = rememberCoroutineScope()
    val allParticipantEvents = (joinedEvents + organizedEvents).distinctBy { it.id }
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Мероприятия", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchChanged,
            placeholder = { Text("Поиск мероприятий") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.height(12.dp))
        val filters = listOf("Все", "Сегодня", "На неделе", "В этом месяце")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            filters.forEach { label ->
                val isSelected = selectedFilter == label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) colorScheme.primary else colorScheme.surfaceVariant
                        )
                        .clickable { viewModel.onFilterSelected(label) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) colorScheme.onPrimary else colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Все мероприятия", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (events.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(events) { event ->
                    val isJoined = joinedEvents.any { it.id == event.id }
                    val isUserOrganizer = organizedEvents.any { it.id == event.id }
                    val canJoin = !isJoined && event.creatorId != viewModel.currentUserId && event.teamId == viewModel.currentUserTeamId
                    val painter = if (!event.imageUri.isNullOrBlank()) {
                        rememberAsyncImagePainter(Uri.parse(event.imageUri))
                    } else {
                        painterResource(id = R.drawable.testew)
                    }
                    EventCardItem(
                        event = event,
                        painter = painter,
                        onClick = { viewModel.onEventClick(event) },
                        onJoin = {
                            viewModel.joinEvent(event.id) {
                                scope.launch { snackbarHostState.showSnackbar(it) }
                            }
                        },
                        showJoinButton = canJoin,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        } else {
            Text(
                text = "Нет мероприятий по выбранному фильтру",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("Вы участвуете", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (allParticipantEvents.isNotEmpty()) {
            allParticipantEvents.forEach { event ->
                MyEventItem(
                    event = event,
                    onClick = { viewModel.onEventClick(event) },
                    onDelete = {
                        if (event.creatorId == viewModel.currentUserId) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Вы не можете покинуть своё мероприятие")
                            }
                        } else {
                            viewModel.leaveEvent(event.id) {
                                scope.launch { snackbarHostState.showSnackbar(it) }
                            }
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
            }
        } else {
            Text(
                text = "Вы пока не участвуете в мероприятиях",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        if (isOrganizer) {
            Text("Твои мероприятия", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (organizedEvents.isNotEmpty()) {
                organizedEvents.forEach { event ->
                    MyEventItem(
                        event = event,
                        onClick = { viewModel.onEventClick(event) },
                        onDelete = null
                    )
                    Spacer(Modifier.height(8.dp))
                }
            } else {
                Text(
                    text = "У вас пока нет созданных мероприятий",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
    selectedEvent?.let { event ->
        AlertDialog(
            onDismissRequest = viewModel::onDialogDismiss,
            confirmButton = {
                TextButton(onClick = viewModel::onDialogDismiss) {
                    Text("ОК")
                }
            },
            title = { Text(event.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(event.locationName)
                    val parsedDateTime = DateTimeUtils.parseIsoFormatted(event.dateTime)
                    val displayDateTime = parsedDateTime?.let { DateTimeUtils.formatDisplay(it) } ?: "Ошибка загрузки времени"
                    Text(displayDateTime)
                    Spacer(Modifier.height(8.dp))
                    Text(event.description)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}