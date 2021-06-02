package com.johnturkson.sync.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnturkson.sync.data.Account
import com.johnturkson.sync.data.AccountRepository
import com.johnturkson.sync.data.computeOTP
import com.johnturkson.sync.ui.CodeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class HomeViewModel @Inject internal constructor(private val accountRepository: AccountRepository) : ViewModel() {
    
    private val interval = 30000L
    private val offset = 1000L
    private val period = 3L
    private val delta = period.toFloat() / interval.toFloat()
    private val completion = 1f
    
    private val internalUpdate = MutableSharedFlow<Boolean>()
    private val internalProgress = MutableStateFlow(((System.currentTimeMillis() - offset) % interval) / interval.toFloat())
    private val internalSearch = MutableStateFlow("")
    private val internalAccounts = MutableStateFlow<List<Account>>(emptyList())
    private val internalCodes = MutableStateFlow<List<CodeState>>(emptyList())
    private val internalSelected = MutableStateFlow<List<Account>>(emptyList())
    private val internalDisplayed = MutableStateFlow<List<CodeState>>(emptyList())
    
    val progress = internalProgress.asStateFlow()
    val selected = internalSelected.asStateFlow()
    val search = internalSearch.asStateFlow()
    val accounts = internalAccounts.asStateFlow()
    val codes = internalCodes.asStateFlow()
    val displayed = internalDisplayed.asStateFlow()
    
    init {
        fixedRateTimer(period = period) {
            val currentProgress = internalProgress.value + delta
            if (currentProgress >= completion) viewModelScope.launch { internalUpdate.emit(true) }
            internalProgress.value = currentProgress % completion
        }
        
        accountRepository.getAccounts().onEach { accounts -> internalAccounts.emit(accounts) }.launchIn(viewModelScope)
        
        internalUpdate.onEach { updateCodes() }.launchIn(viewModelScope)
        
        internalAccounts.onEach { updateCodes() }.launchIn(viewModelScope)
        
        internalCodes.onEach { updateDisplayed() }.launchIn(viewModelScope)
        
        internalSearch.onEach { updateDisplayed() }.launchIn(viewModelScope)
        
        internalSelected.onEach { updateSelected() }.launchIn(viewModelScope)
    }
    
    private fun updateCodes() {
        internalCodes.value = buildCodes(internalAccounts.value)
    }
    
    private fun updateSelected() {
        internalCodes.value = buildCodes(internalAccounts.value)
    }
    
    private fun updateDisplayed() {
        internalDisplayed.value = internalCodes.value.matching(internalSearch.value)
    }
    
    fun search(value: String) {
        internalSearch.value = value
        viewModelScope.launch {
            internalDisplayed.emit(internalCodes.value.matching(value))
        }
    }
    
    fun toggleSelection(state: CodeState) {
        if (state.account in selected.value) {
            internalSelected.value -= state.account
        } else {
            internalSelected.value += state.account
        }
    }
    
    private fun List<CodeState>.matching(value: String): List<CodeState> {
        return this.filter { state -> state.matches(value) }
    }
    
    private fun CodeState.matches(value: String): Boolean {
        if (value.isEmpty()) return true
        val query = value.toLowerCase(Locale.ROOT)
        val issuer = this.account.issuer.toLowerCase(Locale.ROOT)
        val name = this.account.name.toLowerCase(Locale.ROOT)
        return query in issuer || query in name
    }
    
    private fun buildCodes(accounts: List<Account>): List<CodeState> {
        return accounts.map { account -> CodeState(account, account.computeOTP(), account in selected.value) }
    }
}
