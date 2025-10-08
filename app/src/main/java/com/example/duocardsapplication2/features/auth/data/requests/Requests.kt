package com.example.duocardsapplication2.features.auth.data.requests

import com.example.duocardsapplication2.features.auth.domain.model.User

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
data class RegisterResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)

data class LogoutRequest(
    val token: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String
)

data class UserResponse(
    val user: User
)