package com.johnturkson.sync.ui.biometrics

import androidx.biometric.BiometricPrompt

class BiometricsAuthenticationCallback(
    private val onError: (errorCode: Int, errorString: CharSequence) -> Unit = { _, _ -> },
    private val onFailure: () -> Unit = {},
    private val onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
) : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        onError(errorCode, errString)
    }
    
    override fun onAuthenticationFailed() {
        onFailure()
    }
    
    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onSuccess(result)
    }
}
