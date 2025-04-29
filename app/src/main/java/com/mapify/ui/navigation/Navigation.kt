package com.mapify.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import com.mapify.utils.SharedPreferencesUtils
import com.mapify.viewmodel.UsersViewModel

@Composable
fun Navigation(
    usersViewModel: UsersViewModel
) {

    val context = LocalContext.current
    val navController = rememberNavController()
    val user = SharedPreferencesUtils.getPreference(context)

    val startDestination: RouteScreen = if (user.isNotEmpty()) RouteScreen.Home else RouteScreen.Login

    val isAdmin = rememberSaveable { mutableStateOf(false) }
    val userId = rememberSaveable { mutableStateOf("1") }

    Surface {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<RouteScreen.Login> {
                LoginScreen(
                    usersViewModel = usersViewModel,
                    navigateToRegistration = {
                        navController.navigate(RouteScreen.Registration)
                    },
                    navigateToHome = { adminValue, userIdValue ->
                        isAdmin.value = adminValue
                        userId.value = userIdValue
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
                    usersViewModel = usersViewModel,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<RouteScreen.Home> {
                HomeScreen(
                    isAdmin = isAdmin.value,
                    userId = userId.value,
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
                    navigateToConversation = { conversationId ->
                        navController.navigate(RouteScreen.Conversation(conversationId))
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
                    },
                    isAdmin = isAdmin.value,
                    userId = userId.value
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
                    onUserSelected = { conversationId ->
                        navController.navigate(RouteScreen.Conversation(conversationId))
                    }
                )
            }
            composable<RouteScreen.Conversation> { backStackEntry ->
                val args = backStackEntry.toRoute<RouteScreen.Conversation>()
                ConversationScreen(
                    conversationId = args.conversationId,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}