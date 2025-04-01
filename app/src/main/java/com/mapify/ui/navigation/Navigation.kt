package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen

@Composable
fun Navigation(){

    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController,
            startDestination = RouteScreen.Login
        ) {
            composable<RouteScreen.Login> {
                LoginScreen()
            }
            composable<RouteScreen.Registration> {
                RegistrationScreen()
            }
        }
    }
}