package com.example.fitnessappandroid.core.navigation

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