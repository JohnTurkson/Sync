package com.johnturkson.sync.ui.biometrics

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun Biometrics(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)
    val info = with(BiometricPrompt.PromptInfo.Builder()) {
        setTitle("Unlock with Biometrics")
        setAllowedAuthenticators(BIOMETRIC_STRONG)
        setNegativeButtonText("Cancel")
        build()
    }
    val prompt = BiometricPrompt(
        context as FragmentActivity,
        executor,
        BiometricsAuthenticationCallback {
            val destination = navController.previousBackStackEntry?.destination?.route ?: "Home"
            navController.navigate(destination) {
                popUpTo("Biometrics") { inclusive = true }
                launchSingleTop = true
            }
        },
    )
    
    // TODO check if biometrics are enrolled
    
    BackHandler {
        context.moveTaskToBack(true)
    }
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) prompt.authenticate(info)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    
    Scaffold {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            Box(modifier = Modifier.height(128.dp).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Button(onClick = { prompt.authenticate(info) }) {
                    Text("Unlock")
                }
            }
        }
    }
}
