package com.johnturkson.sync.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCodeRepository @Inject constructor(private val codeDao: CodeDao) : CodeRepository {
    override fun getCodes(): Flow<Code> {
        return codeDao.getCodes()
    }
    
    override suspend fun addCode(code: Code) {
        codeDao.addCode(code)
    }
}

