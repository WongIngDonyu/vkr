package com.example.vkr.presentation.screens.home2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vkr.R
import com.example.vkr.data.repository.FeedRepository
import com.example.vkr.data.repository.TeamRepository
import com.example.vkr.data.repository.UserRepository
import com.example.vkr.presentation.components.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen2(navController: NavController, userRepository: UserRepository, teamRepository: TeamRepository, feedRepository: FeedRepository, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel2 = viewModel(
        factory = HomeViewModelFactory(userRepository = userRepository, teamRepository = teamRepository, feedRepository = feedRepository)
    )
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadUserData()
        viewModel.loadLeaderboard()
        viewModel.loadEcoHero()
    }
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val userLeaderboard by viewModel.userLeaderboard.collectAsState()
    val teamLeaderboard by viewModel.teamLeaderboard.collectAsState()
    val ecoHero by viewModel.ecoHero.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val user by viewModel.user.collectAsState()
    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        item {
            Column {
                Text(
                    text = "Добро пожаловать в",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Чистый Двор",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            user?.let {
                Text(
                    text = "Привет, ${it.name}!",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(8.dp))
            val sheetState = rememberModalBottomSheetState()
            val coroutineScope = rememberCoroutineScope()
            var showSheet by remember { mutableStateOf(false) }
            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Зарабатывай баллы легко!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        EarningCard("Присоединяйся к команде", "Стань частью эко-сообщества и начни зарабатывать очки вместе", R.drawable.qww)
                        Spacer(Modifier.height(12.dp))
                        EarningCard("Участвуй в мероприятиях", "Получай до 100 баллов за активное участие в каждом событии", R.drawable.sss)
                        Spacer(Modifier.height(12.dp))
                        EarningCard("Завоёвывай топ", "Набирай очки и поднимайся в таблице лидеров — стань героем!", R.drawable.qwe)
                    }
                }
            }
            Button(
                onClick = { showSheet = true },
                shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6244FF))
            ) {
                Text("Как заработать баллы?", color = Color.White)
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Таблица лидеров",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabItem("Пользователи", selectedTab == LeaderboardTab.USERS) {
                    viewModel.selectTab(LeaderboardTab.USERS)
                }
                TabItem("Команды", selectedTab == LeaderboardTab.TEAMS) {
                    viewModel.selectTab(LeaderboardTab.TEAMS)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PeriodTab("Неделя", selectedPeriod == LeaderboardPeriod.WEEK) {
                    viewModel.selectPeriod(LeaderboardPeriod.WEEK)
                }
                PeriodTab("Месяц", selectedPeriod == LeaderboardPeriod.MONTH) {
                    viewModel.selectPeriod(LeaderboardPeriod.MONTH)
                }
                PeriodTab("Все время", selectedPeriod == LeaderboardPeriod.ALL_TIME) {
                    viewModel.selectPeriod(LeaderboardPeriod.ALL_TIME)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        val topIcons = listOf(R.drawable.test11, R.drawable.test12, R.drawable.test13)
        if (selectedTab == LeaderboardTab.USERS) {
            itemsIndexed(userLeaderboard.take(3)) { index, user ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    if (user.avatarUri.isNullOrBlank()) {
                        val imageRes = topIcons.getOrNull(index)

                        if (imageRes != null) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Медаль за ${index + 1} место",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCCCCCC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(user.avatarUri),
                            contentDescription = user.nickname,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = user.nickname,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Баллы: ${user.points}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            itemsIndexed(teamLeaderboard.take(3)) { index, team ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val imageRes = topIcons.getOrNull(index)
                    if (imageRes != null) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "Медаль за ${index + 1} место",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBBBBBB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = team.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Баллы: ${team.points}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        ecoHero?.let { hero ->
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Герой недели",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.test1),
                            contentDescription = hero.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Ник: ${hero.nickname}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    shadow = Shadow(Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                                ),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Баллы: ${hero.points}",
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    shadow = Shadow(Color.Black, offset = Offset(1f, 1f), blurRadius = 2f)
                                )
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Недавние активности", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }

        items(activities.take(3)) { activity ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val iconRes = when {
                    activity.description.contains("вступил в команду", ignoreCase = true) -> R.drawable.test21
                    activity.description.contains("присоединился к мероприятию", ignoreCase = true) -> R.drawable.test22
                    else -> R.drawable.ima1
                }
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = activity.user,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${activity.user} ${activity.description}")
                    val parsedTime = DateTimeUtils.parseServerFormatted(activity.time)
                    val displayTime = parsedTime?.let { DateTimeUtils.formatDisplay(it) } ?: "Ошибка загрузки времени"
                    Text(displayTime, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun TabItem(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color(0xFFE0E0E0) else Color.Transparent
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PeriodTab(label: String, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color(0xFFD6D6F5) else Color.Transparent
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EarningCard(title: String, description: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}