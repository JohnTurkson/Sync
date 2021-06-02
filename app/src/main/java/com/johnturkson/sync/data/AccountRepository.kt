package com.johnturkson.sync.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    
    suspend fun addAccount(account: Account)
    
    suspend fun removeAccount(account: Account)
}
