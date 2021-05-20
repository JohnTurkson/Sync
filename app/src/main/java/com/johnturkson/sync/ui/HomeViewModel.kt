package com.johnturkson.sync.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnturkson.sync.data.Account
import com.johnturkson.sync.data.AccountRepository
import com.johnturkson.sync.data.computeOTP
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class HomeViewModel @Inject internal constructor(accountRepository: AccountRepository) : ViewModel() {
    private val interval = 30000L
    private val period = 3L
    private val delta = period.toFloat() / interval.toFloat()
    private val internalProgress = MutableStateFlow((System.currentTimeMillis() % interval) / interval.toFloat())
    private val internalSelected = MutableStateFlow<List<Account>>(emptyList())
    private val internalSearch = MutableStateFlow("")
    private val internalAccounts = accountRepository.getAccounts().collectAsMutableStateFlow(emptyList())
    private val internalCodes = internalAccounts.map { accounts -> buildCodes(accounts) }.collectAsMutableStateFlow(emptyList())
    val progress = internalProgress.asStateFlow()
    val selected = internalSelected.asStateFlow()
    val search = internalSearch.asStateFlow()
    val accounts = internalAccounts.asStateFlow()
    val codes = internalCodes.asStateFlow()
    
    init {
        fixedRateTimer(period = period) {
            val currentProgress = internalProgress.value + delta
            if (currentProgress >= 1f) updateCodes()
            internalProgress.value = currentProgress % 1f
        }
    }
    
    fun search(value: String) {
        internalSearch.value = value
    }
    
    fun toggleSelection(state: CodeState) {
        if (state.account in selected.value) {
            internalSelected.value -= state.account
        } else {
            internalSelected.value += state.account
        }
        updateCodes()
    }
    
    private fun updateCodes() {
        val updatedCodes = buildCodes(accounts.value)
        internalCodes.value = updatedCodes
    }
    
    private fun buildCodes(accounts: List<Account>): List<CodeState> {
        return accounts.map { account -> CodeState(account, account.computeOTP(), account in selected.value) }
    }
    
    private fun <T> Flow<T>.collectAsMutableStateFlow(
        initialValue: T,
        scope: CoroutineScope = viewModelScope,
        started: SharingStarted = Eagerly,
    ): MutableStateFlow<T> {
        val mutableFlow = MutableStateFlow(initialValue)
        val initialFlow = this.stateIn(scope, started, initialValue)
        viewModelScope.launch { initialFlow.collect { value -> mutableFlow.value = value } }
        return mutableFlow
    }
}
