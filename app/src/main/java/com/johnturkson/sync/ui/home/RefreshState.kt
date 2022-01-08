package com.johnturkson.sync.ui.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer

class RefreshState(
    private val externalScope: CoroutineScope,
    private val interval: Long = 30000L,
) {
    private val offset = 1000L
    private val period = 3L
    private val delta = period.toFloat() / interval.toFloat()
    private val completion = 1f
    
    private val _progress = MutableStateFlow(computeProgress())
    private val _refresh = MutableSharedFlow<Unit>()
    
    val progress = _progress.asStateFlow()
    val refresh = _refresh.asSharedFlow()
    
    init {
        fixedRateTimer(period = period) {
            val currentProgress = _progress.value + delta
            if (currentProgress >= completion) externalScope.launch { _refresh.emit(Unit) }
            _progress.value = currentProgress % completion
        }
    }
    
    private fun computeProgress(time: Long = System.currentTimeMillis()): Float {
        return ((time - offset) % interval) / interval.toFloat()
    }
}
