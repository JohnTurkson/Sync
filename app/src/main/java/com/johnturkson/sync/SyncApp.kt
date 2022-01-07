package com.johnturkson.sync

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johnturkson.sync.ui.home.Home
import com.johnturkson.sync.ui.scanner.Scanner
import com.johnturkson.sync.ui.theme.SyncTheme

@ExperimentalMaterial3Api
@Composable
fun SyncApp() {
    val navController = rememberNavController()
    
    SyncTheme {
        NavHost(navController = navController, startDestination = "Home") {
            composable("Home") { Home(navController, hiltViewModel()) }
            composable("Scanner") { Scanner(navController, hiltViewModel()) }
        }
    }
}
