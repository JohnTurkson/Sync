package com.johnturkson.sync.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnturkson.sync.android.data.Account
import com.johnturkson.sync.android.data.AccountRepository
import com.johnturkson.sync.android.data.computeAccountCode
import com.johnturkson.sync.android.ui.state.ProgressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject internal constructor(accountRepository: AccountRepository) : ViewModel() {
    private val _progressState = ProgressState(viewModelScope)
    private val _searchState = MutableStateFlow("")
    private val _accounts = MutableStateFlow(listOf<Account>())
    private val _codes = MutableStateFlow(mapOf<Account, String>())
    private val _displayed = MutableStateFlow(listOf<Account>())
    private val _selected = MutableStateFlow(setOf<Account>())
    
    val progressState = _progressState
    val searchState = _searchState.asStateFlow()
    val accounts = _accounts.asStateFlow()
    val codes = _codes.asStateFlow()
    val displayed = _displayed.asStateFlow()
    val selected = _selected.asStateFlow()
    
    init {
        accountRepository.getAccounts().onEach { accounts -> _accounts.emit(accounts) }.launchIn(viewModelScope)
        _accounts.onEach { updateAccounts() }.launchIn(viewModelScope)
        _progressState.reset.onEach { updateAccountCodes() }.launchIn(viewModelScope)
        _searchState.onEach { updateDisplayedAccounts() }.launchIn(viewModelScope)
    }
    
    private fun updateAccounts() {
        updateAccountCodes()
        updateDisplayedAccounts()
    }
    
    private fun updateAccountCodes() {
        _codes.value = buildMap { _accounts.value.forEach { account -> put(account, account.computeAccountCode()) } }
    }
    
    private fun updateDisplayedAccounts() {
        _displayed.value = _accounts.value.filter { account ->
            searchState.value in account.name || searchState.value in account.issuer
        }
    }
    
    fun setSearchState(search: String) {
        _searchState.value = search
    }
    
    fun toggleAccountSelected(account: Account) {
        val selections = _selected.value
        _selected.value = if (account in selections) selections - account else selections + account
    }
}
