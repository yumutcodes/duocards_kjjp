package com.example.duocardsapplication2.features.auth.data

import com.example.duocardsapplication2.core.utiluties.error.ErrorMapper
import com.example.duocardsapplication2.core.utiluties.result.Resource
import com.example.duocardsapplication2.features.auth.data.dto.LoginRequest
import com.example.duocardsapplication2.features.auth.data.dto.LoginResponse
import com.example.duocardsapplication2.features.auth.data.dto.RegisterRequest
import com.example.duocardsapplication2.features.auth.data.dto.RegisterResponse
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.io.IOException

class AuthRepositoryImpl @Inject constructor(private val api: AuthApiService,  private val errorMapper: ErrorMapper) : IAuthRepository {
    override  fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading)
        try {
            val res = api.login(loginRequest)
            if (res.isSuccessful) {
                res.body()?.let { emit(Resource.Success(it)) }
                    ?: emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
            } else {
                emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
            }
        } catch (t: Throwable) {
            emit(Resource.Error(errorMapper.mapToAppError(t)))
        }
    }.flowOn(Dispatchers.IO)

    override fun register(
        registerRequest: RegisterRequest
    ): Flow<Resource<RegisterResponse>> = flow {
        emit(Resource.Loading)
        try {
            val res = api.register(registerRequest)
            if (res.isSuccessful) {
                res.body()?.let { emit(Resource.Success(it)) }
                    ?: emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
            } else {
                emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
            }
        } catch (t: Throwable) {
            emit(Resource.Error(errorMapper.mapToAppError(t)))
        }
    }.flowOn(Dispatchers.IO)
}