package com.example.fitnessappandroid.core.utiluties.ui

sealed interface UiState<out T> {
    object Loading : UiState<Nothing> // Yükleniyor durumu
    data class Success<T>(val data: T) : UiState<T> // Başarılı durumu, veri içerir
    data class Error(val message: UiText) : UiState<Nothing> // Hata durumu, hata mesajı içerir
    object Idle : UiState<Nothing> // Başlangıç durumu
}