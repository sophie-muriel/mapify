package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.ExploreScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.HomeScreen
import com.mapify.ui.screens.ProfileScreen
import com.mapify.ui.screens.ReportLocationScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController, startDestination = RouteScreen.Explore
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
                HomeScreen(
                    navigateToProfile = {
                        navController.navigate(RouteScreen.Profile)
                    },
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    })
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
            composable<RouteScreen.Profile> {
                ProfileScreen(
                    navigateToHome = {
                        navController.navigate(RouteScreen.Home)
                    }
                )
            }
            composable<RouteScreen.Explore> {
                ExploreScreen()
            }
        }
    }
}