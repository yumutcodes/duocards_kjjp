package com.example.duocardsapplication2.features.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val profileImageUrl: String? = null,
    val createdAt: Long? = null
)