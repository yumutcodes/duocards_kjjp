package com.example.fitnessappandroid.features.auth.domain


import com.example.fitnessappandroid.core.utiluties.result.Resource
import com.example.fitnessappandroid.features.auth.data.dto.LoginRequest
import com.example.fitnessappandroid.features.auth.data.dto.LoginResponse
import com.example.fitnessappandroid.features.auth.data.dto.RegisterRequest
import com.example.fitnessappandroid.features.auth.data.dto.RegisterResponse
import kotlinx.coroutines.flow.Flow

/*
Result<LoginResponse> yerine kendi yazacağın reosurce ya da appresult ile sar
üsttekini sakın yapma result kotlinin değil retrofitin sınıfı
 */
interface IAuthRepository {
    fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>>
    fun register(registerRequest: RegisterRequest): Flow<Resource<RegisterResponse>>
}