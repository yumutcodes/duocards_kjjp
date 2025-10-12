package com.example.duocardsapplication2.core.network

object NetworkConstants {
    const val BASE_URL = "http://10.0.2.2:5000/api/"
    
    // Timeout constants (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // API Endpoints
    object Endpoints {
        const val AUTH_LOGIN = "auth/login"
        const val AUTH_REGISTER = "auth/register"
        const val AUTH_REFRESH = "auth/refresh"
    }
    
    // Headers
    object Headers {
        const val AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val CONTENT_TYPE = "Content-Type"
        const val APPLICATION_JSON = "application/json"
    }
}
