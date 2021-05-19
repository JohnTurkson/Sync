package com.johnturkson.sync.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeDao {
    @Query("SELECT * FROM codes")
    fun getCodes(): Flow<Code>
    
    @Insert
    suspend fun addCode(code: Code)
}
