package com.johnturkson.sync.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject internal constructor() : ViewModel() {
    
    private val internalAuthenticated = MutableStateFlow(false)
    val authenticated = internalAuthenticated.asStateFlow()
    
    fun authenticate() {
        internalAuthenticated.value = true
    }
    
    fun unauthenticate() {
        internalAuthenticated.value = false
    }
}
