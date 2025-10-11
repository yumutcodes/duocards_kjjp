package com.example.duocardsapplication2.features.auth.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocardsapplication2.core.data.TokenManager
import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.core.utiluties.result.Resource
import com.example.duocardsapplication2.features.auth.data.dto.RegisterRequest
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: IAuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(newFullName: String) {
        _uiState.update { currentState ->
            currentState.copy(fullNameText = newFullName)
        }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { currentState ->
            currentState.copy(emailText = newEmail)
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(passwordText = newPassword)
        }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(confirmPasswordText = newConfirmPassword)
        }
    }

    fun onRegisterButtonClicked() {
        viewModelScope.launch {
            // Prevent duplicate submissions while loading
            if (_uiState.value.registerState is UiState.Loading) return@launch

            // Validation
            val fullName = _uiState.value.fullNameText.trim()
            val email = _uiState.value.emailText.trim()
            val password = _uiState.value.passwordText
            val confirmPassword = _uiState.value.confirmPasswordText

            val isFullNameValid = fullName.isNotEmpty() && fullName.length >= 2
            val isEmailValid = email.contains("@") && email.contains(".")
            val isPasswordValid = password.length >= 6
            val isPasswordsMatch = password == confirmPassword && password.isNotEmpty()

            _uiState.value = _uiState.value.copy(
                isFullNameValid = isFullNameValid,
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid,
                isPasswordsMatch = isPasswordsMatch
            )

            if (!isFullNameValid || !isEmailValid || !isPasswordValid || !isPasswordsMatch) return@launch

            // Call repository
            repo.register(RegisterRequest(fullName = fullName, email = email, password = password))
                .collectLatest { res ->
                    when (res) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                registerState = UiState.Loading
                            )
                        }
                        is Resource.Success -> {
                            // Save tokens
                            tokenManager.saveTokens(
                                accessToken = res.data.token,
                                refreshToken = res.data.refreshToken
                            )
                            _uiState.value = _uiState.value.copy(
                                registerState = UiState.Success(res.data)
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                registerState = UiState.Error(res.error.uiText)
                            )
                        }
                    }
                }
        }
    }
}

