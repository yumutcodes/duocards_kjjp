package com.example.fitnessappandroid.core.di

import com.example.fitnessappandroid.features.auth.data.AuthRepositoryImpl
import com.example.fitnessappandroid.features.auth.domain.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): IAuthRepository
}
