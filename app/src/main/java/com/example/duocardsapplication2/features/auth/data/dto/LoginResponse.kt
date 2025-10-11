package com.example.duocardsapplication2.features.auth.data.dto
import com.example.duocardsapplication2.features.auth.domain.model.User
import kotlinx.serialization.Serializable  // ← EKLEYİN

@Serializable
data class LoginResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)


