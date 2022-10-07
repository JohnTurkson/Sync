package com.johnturkson.sync.android.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository @Inject constructor(private val accountDao: AccountDao) : AccountRepository {
    override fun getAccounts(): Flow<List<Account>> {
        return accountDao.getAccounts()
    }
    
    override suspend fun addAccount(account: Account) {
        accountDao.addAccount(account)
    }
    
    override suspend fun removeAccount(account: Account) {
        accountDao.removeAccount(account)
    }
}
