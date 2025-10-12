package com.example.duocardsapplication2.features.auth.login.presentation

import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.features.auth.data.dto.LoginResponse


data class LoginUiState(
    val emailText: String = "",
    val isEmailValid: Boolean = true,
    val passwordText: String = "",
    val isPasswordValid: Boolean = true,
    val loginState: UiState<LoginResponse> = UiState.Idle
)