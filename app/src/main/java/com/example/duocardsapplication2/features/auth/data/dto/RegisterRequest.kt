package com.example.duocardsapplication2.features.auth.data.dto

import com.squareup.moshi.Json

data class RegisterRequest(
    @Json(name = "name")
    val name: String,
    @Json(name = "surname")
    val surname: String,
    @Json(name = "email")
    val email: String,
    val password: String
)


