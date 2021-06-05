package com.johnturkson.sync.ui

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class BiometricAuthenticationFragment : Fragment() {
    
    private val viewModel: MainViewModel by activityViewModels()
    
    private val biometricAuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            viewModel.authenticate()
        }
        
        override fun onAuthenticationFailed() {
            viewModel.unauthenticate()
        }
        
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            viewModel.unauthenticate()
        }
    }
    
    override fun onResume() {
        super.onResume()
        showBiometricPrompt()
    }
    
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        
        val biometricPrompt = BiometricPrompt(this, executor, biometricAuthenticationCallback)
        
        val fingerprint = BiometricManager.Authenticators.BIOMETRIC_STRONG
        val pin = BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val allowedAuthenticators = fingerprint or pin
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock")
            .setAllowedAuthenticators(allowedAuthenticators)
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
}
