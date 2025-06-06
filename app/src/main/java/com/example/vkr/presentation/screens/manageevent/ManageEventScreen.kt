package com.example.vkr.presentation.screens.manageevent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun ManageEventScreen(eventId: String, navController: NavController) {
    val context = LocalContext.current
    val viewModel: ManageEventViewModel = viewModel()
    val event = viewModel.event
    val teamName = viewModel.teamName
    val photoUris = viewModel.photoUris
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        viewModel.onImagePicked(uris)
    }
    if (event != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(16.dp)
        ) {
            if (!event.imageUri.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(event.imageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(event.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(event.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text("Место", style = MaterialTheme.typography.labelMedium)
            Text(event.locationName, style = MaterialTheme.typography.bodyLarge, color = colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("Команда", style = MaterialTheme.typography.labelMedium)
            Text(teamName, style = MaterialTheme.typography.bodyLarge, color = colorScheme.onSurfaceVariant)
            if (event.completed) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (event.verified) "Мероприятие подтверждено" else "Мероприятие завершено (на проверке)",
                    color = if (event.verified) colorScheme.primary else colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(24.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!event.completed && !event.verified) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorScheme.surfaceVariant)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", style = MaterialTheme.typography.headlineLarge, color = colorScheme.onSurface)
                        }
                    }
                }
                items(photoUris) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            if (!event.completed && !event.verified) {
                Button(
                    onClick = {
                        viewModel.finishEvent {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text("Завершить мероприятие", color = colorScheme.onPrimary)
                }
                Spacer(Modifier.height(12.dp))
            }
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Назад")
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Мероприятие не найдено", style = MaterialTheme.typography.bodyLarge)
        }
    }
}