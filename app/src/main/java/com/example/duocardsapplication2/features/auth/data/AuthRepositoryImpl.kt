package com.example.duocardsapplication2.features.auth.data

import com.example.duocardsapplication2.features.auth.data.requests.LoginRequest
import com.example.duocardsapplication2.features.auth.data.requests.LoginResponse
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val api: AuthApiService) : IAuthRepository {
    override suspend fun login (loginRequest: LoginRequest): Result<LoginResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val resp = api.login(loginRequest)
            if (resp.isSuccessful) resp.body() ?: throw Exception("Empty body")
            else throw Exception(resp.errorBody()?.string() ?: "Login failed ${resp.code()}")
        }
    }



    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

}