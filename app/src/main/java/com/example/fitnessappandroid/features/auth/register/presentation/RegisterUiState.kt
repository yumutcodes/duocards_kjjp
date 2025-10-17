package com.example.fitnessappandroid.features.auth.register.presentation

import com.example.fitnessappandroid.core.utiluties.ui.UiState
import com.example.fitnessappandroid.features.auth.data.dto.RegisterResponse

data class RegisterUiState(
    val NameText: String = "",
    val SurnameText: String = "",
    val emailText: String = "",
    val passwordText: String = "",
    val confirmPasswordText: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isPasswordsMatch: Boolean = true,
    val registerState: UiState<RegisterResponse> = UiState.Idle
)

