package com.johnturkson.sync.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.fixedRateTimer

// @HiltViewModel
class CodeViewModel : ViewModel() {
    private val interval = 30000L
    private val period = 3L
    private val delta = period.toFloat() / interval.toFloat()
    private val internalProgress = MutableStateFlow((System.currentTimeMillis() % interval) / interval.toFloat())
    private val internalSearch = MutableStateFlow("")
    val progress = internalProgress.asStateFlow()
    val search = internalSearch.asStateFlow()
    
    init {
        fixedRateTimer(period = period) {
            internalProgress.value = (internalProgress.value + delta) % 1f
        }
    }
    
    fun search(value: String) {
        internalSearch.value = value
    }
}
