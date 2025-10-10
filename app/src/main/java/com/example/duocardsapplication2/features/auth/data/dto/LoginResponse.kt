package com.example.duocardsapplication2.features.auth.data.dto

import com.example.duocardsapplication2.features.auth.domain.model.User

data class LoginResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)


