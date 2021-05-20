package com.johnturkson.sync.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    
    suspend fun addAccount(code: Account)
}
