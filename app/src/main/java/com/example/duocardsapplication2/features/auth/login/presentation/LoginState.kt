package com.example.duocardsapplication2.features.auth.login.presentation

import com.example.duocardsapplication2.core.utiluties.ui.UiState
import kotlinx.serialization.Serializable

@Serializable
data class LoginUiState(
    val emailText: String = "",
    val isEmailValid: Boolean = true,
    val passwordText: String = "",
    val isPasswordValid: Boolean = true,
    val loginState: UiState<Nothing> = UiState.Idle
)