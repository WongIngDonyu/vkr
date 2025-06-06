package com.example.vkr.presentation.screens.teamdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vkr.R
import com.example.vkr.presentation.components.EventCardItem
import com.example.vkr.presentation.components.EventCardItem2

@Composable
fun TeamDetailScreen(teamId: String, navController: NavController) {
    val viewModel: TeamDetailViewModel = viewModel()
    val team = viewModel.team
    val users = viewModel.users
    val events = viewModel.events
    val currentUser = viewModel.currentUser
    val selectedEvent = viewModel.selectedEvent
    LaunchedEffect(teamId, currentUser?.teamId) {
        viewModel.loadTeam(teamId)
    }
    team?.let { t ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.test51),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                Text(
                    text = t.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text("Очки: ${t.points}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Проведено мероприятий: ${events.size}", color = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Участники команды", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (users.isEmpty()) {
                Text("Пока нет участников", style = MaterialTheme.typography.bodyMedium)
            } else {
                val pages = users.chunked(4)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(pages) { pageUsers ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.width(300.dp)
                        ) {
                            val rows = pageUsers.chunked(2)
                            rows.forEach { rowUsers ->
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    rowUsers.forEach { user ->
                                        Column(
                                            modifier = Modifier
                                                .width(140.dp)
                                                .height(80.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.Start,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if (user.avatarUri != null) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(user.avatarUri),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Text(user.name, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Мероприятия команды", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (events.isEmpty()) {
                Text("Пока нет мероприятий", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(events.sortedWith(compareBy({ it.verified }, { it.completed }))) { event ->
                        val painter = if (!event.imageUri.isNullOrBlank()) {
                            rememberAsyncImagePainter(event.imageUri)
                        } else {
                            painterResource(id = R.drawable.images)
                        }
                        EventCardItem2(
                            event = event,
                            painter = painter,
                            onClick = {
                                if (event.creatorId == currentUser?.id) {
                                    navController.navigate("manageEvent/${event.id}")
                                } else {
                                    viewModel.selectEvent(event)
                                }
                            },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            when {
                currentUser?.teamId == teamId -> {
                    OutlinedButton(
                        onClick = { viewModel.leaveTeam() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Покинуть команду")
                    }
                }
                currentUser?.teamId == null -> {
                    Button(
                        onClick = { viewModel.joinTeam() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Вступить в команду")
                    }
                }
                else -> {
                    Text(
                        "Вы уже состоите в другой команде.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }
    }
    selectedEvent?.let { event ->
        AlertDialog(
            onDismissRequest = viewModel::onDialogClose,
            confirmButton = {
                TextButton(onClick = viewModel::onDialogClose) {
                    Text("Закрыть")
                }
            },
            title = { Text(event.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text(event.locationName)
                    Text(event.dateTime)
                    Spacer(Modifier.height(8.dp))
                    Text(event.description)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}