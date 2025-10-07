package com.example.duocardsapplication2.features.auth.login.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.duocardsapplication2.features.auth.commoncomposables.AuthConfirmButton
import com.example.duocardsapplication2.features.auth.commoncomposables.AuthTextField
import com.example.duocardsapplication2.features.auth.commoncomposables.NavButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    //navigate fonksiyonu parametresi



) {
    val uiState by viewModel.uiState.collectAsState()
    LoginScreenContent(
        uiState = viewModel.uiState.collectAsState().value,
        onEmailChanged = viewModel::onEmailChanged,
        navigateToRegister = navigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    navigateToRegister: () -> Unit,
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
            labelText = "Email"

        )
        AuthTextField(
            value = uiState.passwordText,
            onInputChanged = onEmailChanged,
            isValid = uiState.isPasswordValid,
            labelText = "Password"
        )
        NavButton(
            infoText = "Don't have an account? Register",
            onClick = navigateToRegister,
            textButtonText = "Register"
        )
        AuthConfirmButton(
            onClick = {},
            buttonText = "Login",
            enabled = true
        )

    }
}
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        uiState = LoginUiState(),
        onEmailChanged = {},
        navigateToRegister = {}
    )
}

