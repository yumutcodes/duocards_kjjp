package com.example.fitnessappandroid.features.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessappandroid.core.data.TokenManager
import com.example.fitnessappandroid.core.utiluties.error.AppError
import com.example.fitnessappandroid.core.utiluties.ui.UiState
import com.example.fitnessappandroid.core.utiluties.result.Resource
import com.example.fitnessappandroid.features.auth.data.dto.LoginRequest
import com.example.fitnessappandroid.features.auth.data.dto.LoginResponse
import com.example.fitnessappandroid.features.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginNavigationEvent {
    object NavigateToHome : LoginNavigationEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: IAuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent: SharedFlow<LoginNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(emailText = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(passwordText = newPassword) }
    }

    fun onAuthConfirmButtonClicked() {
        viewModelScope.launch {
            if (_uiState.value.loginState is UiState.Loading) return@launch
            
            if (!validateAndUpdateState()) return@launch
            
            performLogin()
        }
    }

    private fun validateAndUpdateState(): Boolean {
        val state = _uiState.value
        val email = state.emailText.trim()
        val password = state.passwordText

        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        _uiState.update {
            it.copy(
                isEmailValid = emailError == null,
                isPasswordValid = passwordError == null
            )
        }

        return emailError == null && passwordError == null
    }

    private suspend fun performLogin() {
        val state = _uiState.value
        val request = LoginRequest(
            email = state.emailText.trim(),
            password = state.passwordText
        )

        repo.login(request).collectLatest { resource ->
            when (resource) {
                is Resource.Loading -> handleLoading()
                is Resource.Success -> handleSuccess(resource.data)
                is Resource.Error -> handleError(resource.error)
            }
        }
    }

    private fun handleLoading() {
        _uiState.update { it.copy(loginState = UiState.Loading) }
    }

    private suspend fun handleSuccess(data: LoginResponse) {
        tokenManager.saveTokens(
            accessToken = data.token,
            refreshToken = data.refreshToken
        )
        _uiState.update { it.copy(loginState = UiState.Success(data)) }
        _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
    }

    private fun handleError(error: AppError) {
        _uiState.update { it.copy(loginState = UiState.Error(error.uiText)) }
    }

    private fun validateEmail(email: String): AppError? {
        return when {
            email.isEmpty() -> AppError.FieldRequired("Email")
            !email.contains("@") || !email.contains(".") -> 
                AppError.InvalidFormat("Email", "example@domain.com")
            else -> null
        }
    }

    private fun validatePassword(password: String): AppError? {
        return when {
            password.isEmpty() -> AppError.FieldRequired("Password")
            password.length < 6 -> AppError.InvalidFormat("Password", "at least 6 characters")
            else -> null
        }
    }
}
