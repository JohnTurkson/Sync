package com.johnturkson.sync.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository @Inject constructor(private val codeDao: AccountDao) : AccountRepository {
    override fun getAccounts(): Flow<List<Account>> {
        return codeDao.getAccounts()
    }
    
    override suspend fun addAccount(code: Account) {
        codeDao.addCode(code)
    }
}
