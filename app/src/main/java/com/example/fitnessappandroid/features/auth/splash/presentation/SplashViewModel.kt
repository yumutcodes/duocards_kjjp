package com.example.fitnessappandroid.features.auth.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessappandroid.core.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// State-based yaklaşım: Olaylar değil, durumlar
sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToLogin : SplashUiState
    data object NavigateToHome : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    // Channel yerine StateFlow
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // (İsteğe bağlı) Splash ekranının minimum görünme süresi
            // UX için kullanıcı logo'yu görmüş olur
            delay(1000)
            
            val hasToken = tokenManager.hasValidToken()
            
            // Event göndermek yerine, state'i güncelle
            _uiState.value = if (hasToken) {
                SplashUiState.NavigateToHome
            } else {
                SplashUiState.NavigateToLogin
            }
        }
    }
}

