package com.johnturkson.sync.ui.biometrics

import androidx.biometric.BiometricPrompt

class BiometricsAuthenticationCallback(
    val onAuthenticationError: (errorCode: Int, errorString: String) -> Unit = { _, _ -> },
    val onAuthenticationFailed: () -> Unit = {},
    val onAuthenticated: (result: BiometricPrompt.AuthenticationResult) -> Unit,
) : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
    }
    
    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
    }
    
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onAuthenticated(result)
        super.onAuthenticationSucceeded(result)
    }
}
