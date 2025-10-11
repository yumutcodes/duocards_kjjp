package com.example.duocardsapplication2.features.auth.data.dto

import com.example.duocardsapplication2.features.auth.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)


