package com.johnturkson.sync.ui.biometrics

import androidx.biometric.BiometricManager
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun Biometrics(navController: NavController) {
    fun onAuthenticated() {
        val destination = navController.previousBackStackEntry?.destination?.route ?: "Home"
        navController.navigate(destination) {
            popUpTo("Biometrics") { inclusive = true }
            launchSingleTop = true
        }
    }
    
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val biometricManager = BiometricManager.from(context)
    val allowedBiometricAuthenticators = BIOMETRIC_STRONG
    val biometricPromptInfo = with(BiometricPrompt.PromptInfo.Builder()) {
        setTitle("Unlock with Biometrics")
        setAllowedAuthenticators(allowedBiometricAuthenticators)
        setNegativeButtonText("Cancel")
        build()
    }
    val biometricPrompt = BiometricPrompt(
        context as FragmentActivity,
        BiometricsAuthenticationCallback {
            onAuthenticated()
        },
    )
    
    when (biometricManager.canAuthenticate(allowedBiometricAuthenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        biometricPrompt.authenticate(biometricPromptInfo)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }
            
            Scaffold {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier.height(128.dp).fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Button(onClick = { biometricPrompt.authenticate(biometricPromptInfo) }) {
                            Text("Unlock")
                        }
                    }
                }
            }
        }
        else -> {
            onAuthenticated()
        }
    }
}
