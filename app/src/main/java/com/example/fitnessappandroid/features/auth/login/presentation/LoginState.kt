package com.example.fitnessappandroid.features.auth.login.presentation

import com.example.fitnessappandroid.core.utiluties.ui.UiState
import com.example.fitnessappandroid.features.auth.data.dto.LoginResponse


data class LoginUiState(
    val emailText: String = "",
    val isEmailValid: Boolean = true,
    val passwordText: String = "",
    val isPasswordValid: Boolean = true,
    val loginState: UiState<LoginResponse> = UiState.Idle
)