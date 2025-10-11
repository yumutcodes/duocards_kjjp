package com.example.duocardsapplication2.features.auth.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocardsapplication2.core.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashNavigationState {
    object Idle : SplashNavigationState()
    object NavigateToLogin : SplashNavigationState()
    object NavigateToHome : SplashNavigationState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigationState>(SplashNavigationState.Idle)
    val navigationState: StateFlow<SplashNavigationState> = _navigationState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val hasToken = tokenManager.hasValidToken()
            _navigationState.value = if (hasToken) {
                SplashNavigationState.NavigateToHome
            } else {
                SplashNavigationState.NavigateToLogin
            }
        }
    }
}

