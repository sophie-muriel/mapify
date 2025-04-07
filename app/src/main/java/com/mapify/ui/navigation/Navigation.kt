package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.ExploreScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.HomeScreen
import com.mapify.ui.screens.MessagesScreen
import com.mapify.ui.screens.NotificationsScreen
import com.mapify.ui.screens.ProfileScreen
import com.mapify.ui.screens.ReportLocationScreen
import com.mapify.ui.screens.ReportViewScreen
import com.mapify.ui.screens.SettingsScreen
import com.mapify.ui.screens.SearchFiltersScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController, startDestination = RouteScreen.Messages
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
                    },
                    navigateToExplore = {
                        navController.navigate(RouteScreen.Explore)
                    },
                    navigateToNotifications = {
                        navController.navigate(RouteScreen.Notifications)
                    },
                    navigateToMessages = {
                        navController.navigate(RouteScreen.Messages)
                    }
                )
            }
            composable<RouteScreen.CreateReport> {
                CreateReportScreen(navigateToHome = {
                    navController.navigate(RouteScreen.Home)
                }, navigateToReportLocation = {
                    navController.navigate(RouteScreen.ReportLocation)
                }, navigateToReportView = {
                    navController.navigate(RouteScreen.ReportView(it))
                })
            }
            composable<RouteScreen.ReportLocation> {
                ReportLocationScreen(
                    navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    })
            }
            composable<RouteScreen.ReportView> {
                val args = it.toRoute<RouteScreen.ReportView>()
                ReportViewScreen(
                    reportId = args.reportId, navigateToCreateReport = {
                        navController.navigate(RouteScreen.CreateReport)
                    })
            }
            composable<RouteScreen.Profile> {
                ProfileScreen(
                    navigateToHome = {
                        navController.navigate(RouteScreen.Home)
                    })
            }
            composable<RouteScreen.Explore> {
                ExploreScreen(
                    navigateToCreateReport = {
                    navController.navigate(RouteScreen.CreateReport)
                }, navigateToReportView = {
                    navController.navigate(RouteScreen.ReportView(it))
                }, navigateToSearchFilters = {
                    navController.navigate(RouteScreen.SearchFilters)
                }, navigateToSettings = {
                        navController.navigate(RouteScreen.Settings)
                    }
                )
            }
            composable<RouteScreen.Notifications> {
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
            composable<RouteScreen.Messages> {
                MessagesScreen(
                    navigateToHome = { navController.navigate(RouteScreen.Home) },
                    navigateToExplore = { navController.navigate(RouteScreen.Explore) },
                    navigateToNotifications = { navController.navigate(RouteScreen.Notifications) },
                    navigateToProfile = { navController.navigate(RouteScreen.Profile) }
                )
            }
            composable<RouteScreen.Settings> {
                SettingsScreen(
                    navigateToHome = { navController.navigate(RouteScreen.Home) },
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