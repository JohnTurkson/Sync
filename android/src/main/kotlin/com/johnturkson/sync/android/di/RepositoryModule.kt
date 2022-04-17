package com.johnturkson.sync.android.di

import com.johnturkson.sync.android.data.AccountRepository
import com.johnturkson.sync.android.data.DefaultAccountRepository
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
    abstract fun provideAccountRepository(repository: DefaultAccountRepository): AccountRepository
}
