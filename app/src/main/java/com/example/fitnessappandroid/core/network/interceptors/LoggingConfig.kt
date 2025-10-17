package com.example.fitnessappandroid.core.network.interceptors

import com.example.fitnessappandroid.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

object LoggingConfig {
    
    fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}
