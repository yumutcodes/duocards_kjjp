package com.example.duocardsapplication2.core.network.interceptors

import com.example.duocardsapplication2.BuildConfig
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
