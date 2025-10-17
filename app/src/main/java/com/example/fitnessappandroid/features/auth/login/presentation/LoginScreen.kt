package com.example.fitnessappandroid.features.auth.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessappandroid.core.utiluties.ui.UiState
import com.example.fitnessappandroid.core.utiluties.ui.preview.DevicePreviews
import com.example.fitnessappandroid.core.utiluties.ui.preview.ThemePreviews
import com.example.fitnessappandroid.features.auth.commoncomposables.AuthConfirmButton
import com.example.fitnessappandroid.features.auth.commoncomposables.AuthTextField
import com.example.fitnessappandroid.features.auth.commoncomposables.NavButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Event-driven navigation
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is LoginNavigationEvent.NavigateToHome -> navigateToHome()
            }
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onAuthConfirmButtonClicked = viewModel::onAuthConfirmButtonClicked,
        navigateToRegister = navigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onAuthConfirmButtonClicked: () -> Unit,
    navigateToRegister: () -> Unit
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
        NavButton(
            infoText = "Don't have an account? Register",
            onClick = navigateToRegister,
            textButtonText = "Register"
        )
        AuthConfirmButton(
            onClick = onAuthConfirmButtonClicked,
            buttonText = "Login",
            uiState = uiState.loginState
        )

    }
}
@DevicePreviews
@ThemePreviews
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        uiState = LoginUiState(),
        onEmailChanged = {},
        onPasswordChanged = {},
        onAuthConfirmButtonClicked = {},
        navigateToRegister = {}
    )
}

