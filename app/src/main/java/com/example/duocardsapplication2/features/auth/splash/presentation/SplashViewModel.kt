package com.example.duocardsapplication2.features.auth.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duocardsapplication2.core.data.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashNavigationEvent {
    object NavigateToLogin : SplashNavigationEvent()
    object NavigateToHome : SplashNavigationEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navigationEvent = Channel<SplashNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            val hasToken = tokenManager.hasValidToken()
            val event = if (hasToken) {
                SplashNavigationEvent.NavigateToHome
            } else {
                SplashNavigationEvent.NavigateToLogin
            }
            _navigationEvent.send(event)
        }
    }
}

