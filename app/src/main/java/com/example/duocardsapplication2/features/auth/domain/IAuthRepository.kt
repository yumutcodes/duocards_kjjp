package com.example.duocardsapplication2.features.auth.domain

import com.example.duocardsapplication2.features.auth.data.requests.LoginRequest
import com.example.duocardsapplication2.features.auth.data.requests.LoginResponse
/*
Result<LoginResponse> yerine kendi yazacağın reosurce ya da appresult ile sar
 */
interface IAuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>
    suspend fun register(email: String, password: String): Result<Unit>

}