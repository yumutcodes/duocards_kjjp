package com.example.fitnessappandroid.features.auth.data

import com.example.fitnessappandroid.features.auth.data.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/*
Response<LoginResponse>
body issucesful gibi şeylere olanak tanır doğrusu budur.
 */
interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>
}
