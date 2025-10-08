package com.example.duocardsapplication2.features.auth.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.features.auth.domain.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor
    (
    private val repo: IAuthRepository,
   // private val sessionManager: SessionManager,
            )
    : ViewModel()  {
 private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    fun onEmailChanged(newEmail: String){
        _uiState.value = _uiState.value.copy(emailText = newEmail)
    }
    fun onAuthConfirmButtonClicked(){
        viewModelScope.launch{

        }
      _uiState.value = _uiState.value.copy(loginState = UiState.Loading)
    }
}