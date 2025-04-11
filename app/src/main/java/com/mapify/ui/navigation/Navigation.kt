package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapify.model.Conversation
import com.mapify.ui.screens.ConversationScreen
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.EditReportScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.ProfileScreen
import com.mapify.ui.screens.ReportLocationScreen
import com.mapify.ui.screens.ReportViewScreen
import com.mapify.ui.screens.SearchContactScreen
import com.mapify.ui.screens.SettingsScreen
import com.mapify.ui.screens.SearchFiltersScreen
import com.mapify.ui.users.HomeScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val isAdmin = rememberSaveable { mutableStateOf(false) }

    Surface {
        NavHost(
            navController = navController, startDestination = RouteScreen.Home
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
                    },
                    navigateToSettings = {
                        navController.navigate(RouteScreen.Settings)
                    },
                    navigateToConversation = { conversation ->
                        navController.navigate(RouteScreen.Conversation(conversation.id, conversation.sender))
                    },
                    navigateToReportView = { id, status ->
                        navController.navigate(
                            RouteScreen.ReportView(
                                reportId = id,
                                reportStatus = status
                            )
                        )
                    },
                    navigateToSearchFilters = {
                        navController.navigate(RouteScreen.SearchFilters)
                    },
                    navigateToSearchContact = {
                        navController.navigate(RouteScreen.SearchContact)
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
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<RouteScreen.ReportView> { it ->
                val args = it.toRoute<RouteScreen.ReportView>()
                ReportViewScreen(
                    reportId = args.reportId,
                    reportStatusP = args.reportStatus,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToReportEdit = {
                        navController.navigate(RouteScreen.EditReport(it))
                    },
                    navigateToReportLocation = {
                        navController.navigate(RouteScreen.ReportLocation)
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
            composable<RouteScreen.Settings> {
                SettingsScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToProfile = { navController.navigate(RouteScreen.Profile) },
                    navigateToLogin = {
                        navController.navigate(RouteScreen.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable<RouteScreen.SearchFilters> {
                SearchFiltersScreen(
                    navigateBack = { navController.popBackStack() },
                )
            }
            composable<RouteScreen.EditReport> { it ->
                val args = it.toRoute<RouteScreen.EditReport>()
                EditReportScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToReportLocation = { navController.navigate(RouteScreen.ReportLocation) },
                    reportId = args.reportId
                )
            }
            composable<RouteScreen.SearchContact> {
                SearchContactScreen(
                    navigateBack = { navController.popBackStack() },
                    onUserSelected = { username ->
                        navController.navigate(RouteScreen.Conversation(conversationId = username, senderName = username))
                    }
                )
            }
            composable<RouteScreen.Conversation> { backStackEntry ->
                val args = backStackEntry.toRoute<RouteScreen.Conversation>()
                ConversationScreen(
                    conversation =  Conversation(
                        id = args.conversationId,
                        sender = args.senderName,
                        messages = emptyList()
                    ),
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}