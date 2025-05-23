package com.example.vkr.presentation.screens.splash

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.vkr.R

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = viewModel()
    val navigation = viewModel.shouldNavigate
    val splashImages = listOf(R.drawable.splash1,R.drawable.splash2,R.drawable.splash3,)
    val randomSplashImage = remember { splashImages.random() }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    LaunchedEffect(navigation) {
        when (navigation) {
            SplashViewModel.NavigationTarget.Main -> {
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                }
            }
            SplashViewModel.NavigationTarget.Auth -> {
                navController.navigate("auth") {
                    popUpTo(0)
                }
            }
            null -> {}
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = randomSplashImage),
            contentDescription = "Splash image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}