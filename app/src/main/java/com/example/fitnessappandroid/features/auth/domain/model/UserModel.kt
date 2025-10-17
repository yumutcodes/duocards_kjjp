package com.example.fitnessappandroid.features.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val profileImageUrl: String? = null,
    val createdAt: Long? = null
)