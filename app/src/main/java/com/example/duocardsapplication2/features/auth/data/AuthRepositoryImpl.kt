package com.example.duocardsapplication2.features.auth.data

import com.example.duocardsapplication2.core.utiluties.result.Resource
import com.example.duocardsapplication2.features.auth.data.requests.LoginRequest
import com.example.duocardsapplication2.features.auth.data.requests.LoginResponse
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val api: AuthApiService) : IAuthRepository {
    override suspend fun login (loginRequest: LoginRequest): Resource<LoginResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.login(loginRequest)
            if (response.isSuccessful) {
              re  Resource<LoginResponse>.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        }
    }



    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

}