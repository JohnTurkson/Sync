package com.johnturkson.sync

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.johnturkson.sync.ui.biometrics.Biometrics
import com.johnturkson.sync.ui.home.Home
import com.johnturkson.sync.ui.setup.Setup
import com.johnturkson.sync.ui.theme.SyncTheme

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@Composable
fun SyncApp() {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                navController.navigate("Biometrics") {
                    launchSingleTop = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    SyncTheme {
        NavHost(navController = navController, startDestination = "Biometrics") {
            composable("Home") { Home(navController, hiltViewModel()) }
            composable("Setup") { Setup(navController, hiltViewModel()) }
            composable("Biometrics") { Biometrics(navController) }
        }
    }
}
