package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.HomeScreen
import com.mapify.ui.screens.ReportLocationScreen
import com.mapify.ui.screens.ReportViewScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController, startDestination = RouteScreen.CreateReport
        ) {
            composable<RouteScreen.Login> {
                LoginScreen(navigateToRegistration = {
                    navController.navigate(RouteScreen.Registration)
                }, navigateToHome = {
                    navController.navigate(RouteScreen.Home)
                })
            }
            composable<RouteScreen.Registration> {
                RegistrationScreen(
                    navigateToLogin = {
                        navController.navigate(RouteScreen.Login)
                    })
            }
            composable<RouteScreen.Home> {
                HomeScreen()
            }
            composable<RouteScreen.CreateReport> {
                CreateReportScreen(
                    navigateToHome = {
                    navController.navigate(RouteScreen.Home)
                }, navigateToReportLocation = {
                    navController.navigate(RouteScreen.ReportLocation)
                }, navigateToReportView = {
                    navController.navigate(RouteScreen.ReportView)
                })
            }
            composable<RouteScreen.ReportLocation> {
                ReportLocationScreen(
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    })
            }
            composable<RouteScreen.ReportView> {
                ReportViewScreen(
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    })
            }
        }
    }
}