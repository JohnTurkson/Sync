package com.johnturkson.sync

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johnturkson.sync.theme.AppTheme
import com.johnturkson.sync.ui.home.Home
import com.johnturkson.sync.ui.setup.Setup

@Composable
fun SyncApp() {
    val navController = rememberNavController()
    
    AppTheme {
        NavHost(navController = navController, startDestination = "Home") {
            composable("Home") { Home(navController, hiltViewModel()) }
            composable("Setup") { Setup(navController, hiltViewModel()) }
        }
    }
}
