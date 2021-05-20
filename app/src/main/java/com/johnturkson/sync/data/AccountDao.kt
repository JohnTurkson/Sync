package com.johnturkson.sync.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAccounts(): Flow<List<Account>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCode(code: Account)
}
