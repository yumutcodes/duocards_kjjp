package com.example.fitnessappandroid.features.auth.data.dto

import com.example.fitnessappandroid.features.auth.domain.model.User

data class RegisterResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)


