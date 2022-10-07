package com.johnturkson.sync.android

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.johnturkson.sync.android.ui.biometrics.Biometrics
import com.johnturkson.sync.android.ui.home.Home
import com.johnturkson.sync.android.ui.setup.Setup
import com.johnturkson.sync.android.ui.theme.SyncTheme

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@Composable
fun SyncApp() {
    val navController = rememberNavController()
    
    SyncTheme {
        NavHost(navController = navController, startDestination = "Biometrics") {
            composable("Home") { Home(navController, hiltViewModel()) }
            composable("Setup") { Setup(navController, hiltViewModel()) }
            composable("Biometrics") { Biometrics(navController) }
        }
    }
}
