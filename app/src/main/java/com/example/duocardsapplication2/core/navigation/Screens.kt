package com.example.duocardsapplication2.core.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    object Home : Screens

    @Serializable
    object Register :Screens
    @Serializable
    object Login : Screens
    @Serializable
    object SplashScreen : Screens


}