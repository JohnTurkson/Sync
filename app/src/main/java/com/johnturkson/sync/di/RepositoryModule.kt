package com.johnturkson.sync.di

import com.johnturkson.sync.data.CodeRepository
import com.johnturkson.sync.data.DefaultCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideCodeRepository(repository: DefaultCodeRepository): CodeRepository
}
