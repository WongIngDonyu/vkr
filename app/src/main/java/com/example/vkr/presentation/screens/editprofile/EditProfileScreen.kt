package com.example.vkr.presentation.screens.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vkr.R
import com.example.vkr.data.session.UserSessionManager
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navController: NavController) {
    val viewModel: EditProfileViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val phone by sessionManager.userPhone.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(phone) {
        phone?.let { viewModel.loadUserByPhone(it) }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAvatarChange(uri)
    }
    val avatarPainter = when {
        viewModel.avatarUri != null -> rememberAsyncImagePainter(viewModel.avatarUri)
        !viewModel.user?.avatarUri.isNullOrEmpty() -> rememberAsyncImagePainter(Uri.parse(viewModel.user!!.avatarUri))
        else -> painterResource(id = R.drawable.test31)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Редактирование профиля", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Обновите ваши данные",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colorScheme.surfaceVariant)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = avatarPainter,
                contentDescription = "Аватар",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        TextButton(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Изменить фото")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.fullName,
            onValueChange = viewModel::onFullNameChange,
            label = { Text("Имя") },
            isError = viewModel.fullNameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.fullNameError)
            Text(
                "Имя не может быть пустым",
                color = colorScheme.error,
                fontSize = 12.sp
            )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Никнейм") },
            isError = viewModel.usernameError,
            modifier = Modifier.fillMaxWidth()
        )
        if (viewModel.usernameError)
            Text(
                "Минимум 3 символа",
                color = colorScheme.error,
                fontSize = 12.sp
            )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = viewModel.phone,
            onValueChange = {},
            label = { Text("Телефон") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Badge, contentDescription = "Роль", tint = colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Роль: ${viewModel.user?.role ?: "неизвестна"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.save {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("reloadProfile", true)
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("Сохранить")
            }
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
        }
    }
}