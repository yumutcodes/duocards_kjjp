package com.example.duocardsapplication2.core.utiluties.ui

import kotlinx.serialization.Serializable

@Serializable
sealed interface UiState<out T> {
    @Serializable
    object Loading : UiState<Nothing> // Yükleniyor durumu
    @Serializable
    data class Success<T>(val data: T) : UiState<T> // Başarılı durumu, veri içerir
    @Serializable
    data class Error(val message: UiText) : UiState<Nothing> // Hata durumu, hata mesajı içerir
    @Serializable
    object Idle : UiState<Nothing> // Başlangıç durumu
}