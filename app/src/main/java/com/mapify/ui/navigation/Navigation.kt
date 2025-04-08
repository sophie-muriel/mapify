package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.NotificationsScreen
import com.mapify.ui.screens.ProfileScreen
import com.mapify.ui.screens.ReportLocationScreen
import com.mapify.ui.screens.ReportViewScreen
import com.mapify.ui.screens.SettingsScreen
import com.mapify.ui.screens.SearchFiltersScreen
import com.mapify.ui.users.HomeScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val isAdmin = rememberSaveable { mutableStateOf(false) }

    Surface {
        NavHost(
            navController = navController, startDestination = RouteScreen.Login
        ) {
            composable<RouteScreen.Login> {
                LoginScreen(
                    navigateToRegistration = {
                        navController.navigate(RouteScreen.Registration)
                    },
                    navigateToHome = { adminValue ->
                        isAdmin.value = adminValue
                        navController.navigate(RouteScreen.Home) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<RouteScreen.Registration> {
                RegistrationScreen(
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<RouteScreen.Home> {
                HomeScreen(
                    isAdmin = isAdmin.value,
                    navigateToProfile = {
                        navController.navigate(RouteScreen.Profile)
                    },
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    },
                    navigateToDetail = {
                        navController.navigate(RouteScreen.ReportView(it))
                    }
                )
            }
            composable<RouteScreen.CreateReport> {
                CreateReportScreen(
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToReportLocation = {
                        navController.navigate(RouteScreen.ReportLocation)
                    },
                    navigateToReportView = {
                        navController.navigate(RouteScreen.ReportView(it))
                    }
                )
            }
            composable<RouteScreen.ReportLocation> {
                ReportLocationScreen(
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport) // TODO: change to back
                    }
                )
            }
            composable<RouteScreen.ReportView> {
                val args = it.toRoute<RouteScreen.ReportView>()
                ReportViewScreen(
                    reportId = args.reportId,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<RouteScreen.Profile> {
                ProfileScreen(
                    navigateBack = {
                        navController.popBackStack()
                    },
                    isAdmin = isAdmin.value
                )
            }
            composable<RouteScreen.Notifications> { //TODO: move this entire thing to tabs and add back
                NotificationsScreen(
                    navigateToHome = { navController.navigate(RouteScreen.Home) },
                    navigateToExplore = { navController.navigate(RouteScreen.Explore) },
                    navigateToMessages = { navController.navigate(RouteScreen.Messages) },
                    navigateToProfile = { navController.navigate(RouteScreen.Profile) },
                    navigateToReportView = { reportId ->
                        navController.navigate(RouteScreen.ReportView(reportId))
                    }
                )
            }
            composable<RouteScreen.Settings> {
                SettingsScreen(
                    navigateToHome = { navController.navigate(RouteScreen.Home) }, //TODO: add back
                )
            }
            composable<RouteScreen.SearchFilters> {
                SearchFiltersScreen(
                    navigateToExplore = { navController.navigate(RouteScreen.Explore) },
                )
            }
        }
    }
}