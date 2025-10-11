package com.example.duocardsapplication2.core.di

import com.example.duocardsapplication2.features.auth.data.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    
    /**
     * Provides the main AuthApiService with authentication
     * Used for all authenticated API calls
     */
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
    
    /**
     * Provides an unauthenticated AuthApiService for refresh token calls
     * This prevents circular dependency with TokenAuthenticator
     */
    @Provides
    @Singleton
    @UnauthenticatedClient
    fun provideUnauthenticatedAuthApiService(
        @UnauthenticatedClient retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
