package com.example.fitnessappandroid.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnessappandroid.features.auth.login.presentation.LoginScreen
import com.example.fitnessappandroid.features.auth.register.presentation.RegisterScreen
import com.example.fitnessappandroid.features.auth.splash.presentation.SplashScreen
import com.example.fitnessappandroid.features.home.presentation.HomeScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    Scaffold {innerPadding->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screens.SplashScreen
        ){
            composable<Screens.SplashScreen>{
                SplashScreen(
                    navigateToLogin = {
                        navController.navigate(Screens.Login){
                            popUpTo(Screens.SplashScreen){
                                inclusive = true
                            }
                        }
                    },
                    navigateToHome = {
                        navController.navigate(Screens.Home){
                            popUpTo(Screens.SplashScreen){
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable<Screens.Login>{
                LoginScreen(
                    navigateToRegister = {
                        // Simple navigation - allows back button to work
                        navController.navigate(Screens.Register)
                    },
                    navigateToHome = {
                        navController.navigate(Screens.Home){
                            // Clear entire auth flow from back stack
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable<Screens.Register>{
                RegisterScreen(
                    navigateToLogin = {
                        // Pop back to Login instead of creating new instance
                        navController.popBackStack()
                    },
                    navigateToHome = {
                        navController.navigate(Screens.Home){
                            // Clear entire auth flow from back stack
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable<Screens.Home>{
                HomeScreen()
            }
        }
    }
}