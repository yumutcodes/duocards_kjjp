package com.example.fitnessappandroid.features.auth.register.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessappandroid.core.data.TokenManager
import com.example.fitnessappandroid.core.utiluties.error.AppError
import com.example.fitnessappandroid.core.utiluties.ui.UiState
import com.example.fitnessappandroid.core.utiluties.result.Resource
import com.example.fitnessappandroid.features.auth.data.dto.RegisterRequest
import com.example.fitnessappandroid.features.auth.data.dto.RegisterResponse
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

sealed class RegisterNavigationEvent {
    object NavigateToHome : RegisterNavigationEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: IAuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RegisterNavigationEvent>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent: SharedFlow<RegisterNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onNameChanged(newFullName: String) {
        _uiState.update { it.copy(NameText = newFullName) }
    }
    fun onSurnameChanged(newSurname: String) {
        _uiState.update { it.copy(SurnameText = newSurname) }
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(emailText = newEmail) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(passwordText = newPassword) }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPasswordText = newConfirmPassword) }
    }

    fun onRegisterButtonClicked() {
        viewModelScope.launch {
            if (_uiState.value.registerState is UiState.Loading) return@launch
            
            if (!validateAndUpdateState()) return@launch
            
            performRegistration()
        }
    }

    private fun validateAndUpdateState(): Boolean {
        val state = _uiState.value
        val name = state.NameText.trim()
        val surName = state.SurnameText.trim()
        val email = state.emailText.trim()
        val password = state.passwordText
        val confirmPassword = state.confirmPasswordText


        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val passwordMatchError = validatePasswordMatch(password, confirmPassword)

        _uiState.update {
            it.copy(

                isEmailValid = emailError == null,
                isPasswordValid = passwordError == null,
                isPasswordsMatch = passwordMatchError == null
            )
        }

        return emailError == null &&
               passwordError == null && passwordMatchError == null
    }

    private suspend fun performRegistration() {
        val state = _uiState.value
        val request = RegisterRequest(
            name = state.NameText.trim(),
            surname = state.SurnameText.trim(),
            email = state.emailText.trim(),
            password = state.passwordText
        )

        repo.register(request).collectLatest { resource ->
            when (resource) {
                is Resource.Loading -> handleLoading()
                is Resource.Success -> handleSuccess(resource.data)
                is Resource.Error -> handleError(resource.error)
            }
        }
    }

    private fun handleLoading() {
        _uiState.update { it.copy(registerState = UiState.Loading) }
    }

    private suspend fun handleSuccess(data: RegisterResponse) {
        tokenManager.saveTokens(
            accessToken = data.token,
            refreshToken = data.refreshToken
        )
        _uiState.update { it.copy(registerState = UiState.Success(data)) }
        _navigationEvent.emit(RegisterNavigationEvent.NavigateToHome)
    }

    private fun handleError(error: AppError) {
        _uiState.update { it.copy(registerState = UiState.Error(error.uiText)) }
    }

    private fun validateFullName(fullName: String): AppError? {
        return when {
            fullName.isEmpty() -> AppError.FieldRequired("Full Name")
            fullName.length < 2 -> AppError.InvalidFormat("Full Name", "at least 2 characters")
            else -> null
        }
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

    private fun validatePasswordMatch(password: String, confirmPassword: String): AppError? {
        return when {
            confirmPassword.isEmpty() -> AppError.FieldRequired("Confirm Password")
            password != confirmPassword -> AppError.InvalidFormat("Password", "passwords must match")
            else -> null
        }
    }
}
