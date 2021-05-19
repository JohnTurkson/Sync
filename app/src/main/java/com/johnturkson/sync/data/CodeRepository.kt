package com.johnturkson.sync.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface CodeRepository {
    fun getCodes(): Flow<Code>
    
    suspend fun addCode(code: Code)
}
