package com.johnturkson.sync.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAccounts(): Flow<List<Account>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAccount(code: Account)
    
    @Delete
    suspend fun removeAccount(account: Account)
}
