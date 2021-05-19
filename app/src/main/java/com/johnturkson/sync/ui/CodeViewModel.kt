package com.johnturkson.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnturkson.sync.data.CodeDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class CodeViewModel @Inject internal constructor(codeDao: CodeDao) : ViewModel() {
    private val interval = 30000L
    private val period = 3L
    private val delta = period.toFloat() / interval.toFloat()
    private val internalProgress = MutableStateFlow((System.currentTimeMillis() % interval) / interval.toFloat())
    private val internalSearch = MutableStateFlow("")
    val progress = internalProgress.asStateFlow()
    val search = internalSearch.asStateFlow()
    val codes = codeDao.getCodes()
    
    init {
        fixedRateTimer(period = period) {
            internalProgress.value = (internalProgress.value + delta) % 1f
        }
        
        viewModelScope.launch {
            val debug = System.currentTimeMillis().toString()
            codeDao.addCode(com.johnturkson.sync.data.Code(debug, debug, debug))
        }
    }
    
    fun search(value: String) {
        internalSearch.value = value
    }
}
