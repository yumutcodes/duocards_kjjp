package com.example.duocardsapplication2.features.auth.register.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.core.utiluties.ui.preview.DevicePreviews
import com.example.duocardsapplication2.core.utiluties.ui.preview.ThemePreviews
import com.example.duocardsapplication2.features.auth.commoncomposables.AuthConfirmButton
import com.example.duocardsapplication2.features.auth.commoncomposables.AuthTextField
import com.example.duocardsapplication2.features.auth.commoncomposables.NavButton

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Event-driven navigation
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is RegisterNavigationEvent.NavigateToHome -> navigateToHome()
            }
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onFullNameChanged = viewModel::onFullNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onRegisterButtonClicked = viewModel::onRegisterButtonClicked,
        navigateToLogin = navigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onFullNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onRegisterButtonClicked: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthTextField(
            value = uiState.fullNameText,
            onInputChanged = onFullNameChanged,
            isValid = uiState.isFullNameValid,
            labelText = "Full Name",
            ErrorText = "Invalid full name"
        )
        AuthTextField(
            value = uiState.emailText,
            onInputChanged = onEmailChanged,
            isValid = uiState.isEmailValid,
            labelText = "Email",
            ErrorText = "Invalid email"
        )
        AuthTextField(
            value = uiState.passwordText,
            onInputChanged = onPasswordChanged,
            isValid = uiState.isPasswordValid,
            labelText = "Password",
            ErrorText = "Invalid password"
        )
        AuthTextField(
            value = uiState.confirmPasswordText,
            onInputChanged = onConfirmPasswordChanged,
            isValid = uiState.isPasswordsMatch,
            labelText = "Confirm Password",
            ErrorText = "Passwords do not match"
        )
        NavButton(
            infoText = "Already have an account?",
            onClick = navigateToLogin,
            textButtonText = "Login"
        )
        AuthConfirmButton(
            onClick = onRegisterButtonClicked,
            buttonText = "Register",
            uiState = uiState.registerState
        )
    }
}

@DevicePreviews
@ThemePreviews
@Composable
fun RegisterScreenPreview() {
    RegisterScreenContent(
        uiState = RegisterUiState(),
        onFullNameChanged = {},
        onEmailChanged = {},
        onPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onRegisterButtonClicked = {},
        navigateToLogin = {}
    )
}

