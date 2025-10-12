package com.example.duocardsapplication2.features.auth.splash.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
) {
    // State'i lifecycle-aware şekilde collect et
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State'e göre ne gösterileceğini/yapılacağını belirle
    when (uiState) {
        SplashUiState.Loading -> {
            // Loading durumunda splash içeriğini göster
            SplashScreenContent()
        }
        SplashUiState.NavigateToHome -> {
            // NavigateToHome durumuna geçildiğinde navigate et
            LaunchedEffect(Unit) {
                navigateToHome()
            }
        }
        SplashUiState.NavigateToLogin -> {
            // NavigateToLogin durumuna geçildiğinde navigate et
            LaunchedEffect(Unit) {
                navigateToLogin()
            }
        }
    }
}

@Composable
private fun SplashScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "DuoCards",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator()
    }
}

