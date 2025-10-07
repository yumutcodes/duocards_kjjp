package com.example.duocardsapplication2.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.duocardsapplication2.features.auth.login.presentation.LoginScreen

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

        }
            composable<Screens.Login>{
                LoginScreen(
                    navigateToRegister = {
                        // popuntilli NAVİGATE
                        navController.navigate(Screens.Register){
                            popUpTo(Screens.Login){
                                inclusive = true
                            }
                        }

                    }
                )
            }
        }
    }
}