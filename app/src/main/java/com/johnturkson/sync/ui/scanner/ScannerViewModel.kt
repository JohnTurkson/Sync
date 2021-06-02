package com.johnturkson.sync.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnturkson.sync.data.Account
import com.johnturkson.sync.data.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject internal constructor(private val accountRepository: AccountRepository) : ViewModel() {
    
    fun onAccountSetup(account: Account) {
        viewModelScope.launch { 
            accountRepository.addAccount(account)
        }
    }
}