package com.example.duocardsapplication2.features.auth.register.presentation

import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.features.auth.data.dto.RegisterResponse

data class RegisterUiState(
    val fullNameText: String = "",
    val emailText: String = "",
    val passwordText: String = "",
    val confirmPasswordText: String = "",
    val isFullNameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isPasswordsMatch: Boolean = true,
    val registerState: UiState<RegisterResponse> = UiState.Idle
)

