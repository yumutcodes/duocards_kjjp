package com.example.duocardsapplication2.features.auth.domain


import com.example.duocardsapplication2.core.utiluties.result.Resource
import com.example.duocardsapplication2.features.auth.data.dto.LoginRequest
import com.example.duocardsapplication2.features.auth.data.dto.LoginResponse
import kotlinx.coroutines.flow.Flow

/*
Result<LoginResponse> yerine kendi yazacağın reosurce ya da appresult ile sar
üsttekini sakın yapma result kotlinin değil retrofitin sınıfı
 */
interface IAuthRepository {
        fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    suspend fun register(email: String, password: String): Flow<Result<Unit>>

}