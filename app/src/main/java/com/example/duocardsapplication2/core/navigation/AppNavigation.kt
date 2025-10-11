package com.example.duocardsapplication2.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.duocardsapplication2.features.auth.login.presentation.LoginScreen
import com.example.duocardsapplication2.features.auth.register.presentation.RegisterScreen
import com.example.duocardsapplication2.features.auth.splash.presentation.SplashScreen
import com.example.duocardsapplication2.features.home.presentation.HomeScreen

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
                        navController.navigate(Screens.Register){
                            popUpTo(Screens.Login){
                                inclusive = true
                            }
                        }
                    },
                    navigateToHome = {
                        navController.navigate(Screens.Home){
                            popUpTo(Screens.Login){
                                inclusive = true
                            }
                        }
                    }
                )
            }
            
            composable<Screens.Register>{
                RegisterScreen(
                    navigateToLogin = {
                        navController.navigate(Screens.Login){
                            popUpTo(Screens.Register){
                                inclusive = true
                            }
                        }
                    },
                    navigateToHome = {
                        navController.navigate(Screens.Home){
                            popUpTo(Screens.Register){
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