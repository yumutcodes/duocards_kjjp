package com.example.duocardsapplication2.features.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.core.utiluties.result.Resource
import com.example.duocardsapplication2.core.utiluties.ui.UiText
import com.example.duocardsapplication2.features.auth.data.dto.LoginRequest
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: IAuthRepository,
    // private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(emailText = newEmail)
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(passwordText = newPassword)
    }

    fun onAuthConfirmButtonClicked() {
        viewModelScope.launch {
            // Prevent duplicate submissions while loading
            if (_uiState.value.loginState is UiState.Loading) return@launch

            // Basic validation
            val email = _uiState.value.emailText.trim()
            val password = _uiState.value.passwordText
            val isEmailValid = email.contains("@") && email.contains(".")
            val isPasswordValid = password.length >= 6

            _uiState.value = _uiState.value.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
            if (!isEmailValid || !isPasswordValid) return@launch

            // Map repository Resource<> emissions to UiState
            repo.login(LoginRequest(email = email, password = password))
                .collectLatest { res ->
                    when (res) {
                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                loginState = UiState.Loading
                            )
                        }
                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(
                                loginState = UiState.Success(res.data)
                            )
                        }
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                loginState = UiState.Error(res.error.uiText)
                            )
                        }
                    }
                }
        }
    }
}