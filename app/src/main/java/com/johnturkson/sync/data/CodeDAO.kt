package com.johnturkson.sync.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeDAO {
    @Query("SELECT * FROM codes")
    fun getCodes(): Flow<Code>
}
