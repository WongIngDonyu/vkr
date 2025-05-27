package com.example.vkr.presentation.layout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.vkr.presentation.components.BottomNavigationBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vkr.data.api.RetrofitInstance
import com.example.vkr.data.local.AppDatabase
import com.example.vkr.data.repository.FeedRepository
import com.example.vkr.data.repository.TeamRepository
import com.example.vkr.data.repository.UserRepository
import com.example.vkr.data.session.UserSessionManager
import com.example.vkr.presentation.screens.events.EventsScreen
import com.example.vkr.presentation.screens.home2.HomeScreen2
import com.example.vkr.presentation.screens.profile.ProfileScreen
import com.example.vkr.presentation.screens.search.SearchScreen

@Composable
fun MainScreen(currentRoute: String, navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val session = remember { UserSessionManager(context) }
    val role by session.userRole.collectAsState(initial = null)
    val fabColor = MaterialTheme.colorScheme.primary
    val fabIconTint = MaterialTheme.colorScheme.onPrimary

    val db = remember { AppDatabase.getInstance(context) }

    val userRepository = remember {
        UserRepository(userDao = db.userDao(), teamDao = db.teamDao(), session = session, achievementDao = AppDatabase.getInstance(context).achievementDao())
    }

    val teamRepository = remember {
        TeamRepository(teamDao = db.teamDao(), userDao = db.userDao(), eventDao = db.eventDao(), session = session)
    }

    val feedRepository = remember {
        FeedRepository(RetrofitInstance.feedApi)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        },
        floatingActionButton = {
            if (currentRoute == "events" && role == "ORGANIZER") {
                FloatingActionButton(
                    onClick = { navController.navigate("create_event") },
                    containerColor = fabColor,
                    contentColor = fabIconTint,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать мероприятие",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { padding ->
        when (currentRoute) {
            "home" -> HomeScreen2(navController = navController, userRepository = userRepository, teamRepository = teamRepository, feedRepository = feedRepository, modifier = Modifier.padding(padding))
            "search" -> SearchScreen(navController)
            "events" -> EventsScreen(modifier = Modifier.padding(padding), snackbarHostState = snackbarHostState, navController = navController)
            "profile" -> ProfileScreen(navController, Modifier.padding(padding))
        }
    }
}