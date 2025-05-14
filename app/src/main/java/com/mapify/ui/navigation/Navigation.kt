package com.mapify.ui.navigation

import LocationPermissionWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.mapify.viewmodel.MainViewModel

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> { error("MainViewModel not found!") }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val user = mainViewModel.usersViewModel.loadUser(context)
    val startDestination: RouteScreen = if (user != null) RouteScreen.Home else RouteScreen.Login

    val latitude = rememberSaveable { mutableStateOf<Double?>(null) }
    val longitude = rememberSaveable { mutableStateOf<Double?>(null) }

    Surface {
        CompositionLocalProvider(LocalMainViewModel provides mainViewModel) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            LocationPermissionWrapper(
                currentRoute = currentRoute
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable<RouteScreen.Login> {
                        LoginScreen(
                            navigateToRegistration = {
                                navController.navigate(RouteScreen.Registration)
                            },
                            navigateToHome = {
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
                            navigateToProfile = {
                                navController.navigate(RouteScreen.Profile)
                            },
                            navigateToCreateReport = {
                                navController.navigate(RouteScreen.CreateReport())
                            },
                            navigateToDetail = {
                                navController.navigate(RouteScreen.ReportView(it))
                            },
                            navigateToSettings = {
                                navController.navigate(RouteScreen.Settings)
                            },
                            navigateToConversation = { conversationId, isConversation ->
                                navController.navigate(RouteScreen.Conversation(conversationId, isConversation))
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
                            latitude = latitude.value,
                            longitude = longitude.value,
                            navigateBack = {
                                latitude.value = null
                                longitude.value = null
                                navController.popBackStack()
                            },
                            navigateToReportLocation = {
                                navController.navigate(
                                    RouteScreen.ReportLocation(
                                        latitude = latitude.value,
                                        longitude = longitude.value
                                    )
                                )
                            },
                            navigateToReportView = {
                                navController.navigate(RouteScreen.ReportView(it))
                            }
                        )
                    }
                    composable<RouteScreen.ReportLocation> {
                        val args = it.toRoute<RouteScreen.ReportLocation>()
                        ReportLocationScreen(
                            latitude = args.latitude,
                            longitude = args.longitude,
                            navigateBack = { lat, lng ->
                                latitude.value = lat
                                longitude.value = lng
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
                            }
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
                            latitude = latitude.value,
                            longitude = longitude.value,
                            navigateBack = {
                                latitude.value = null
                                longitude.value = null
                                navController.popBackStack()
                            },
                            navigateToReportLocation = { navController.navigate(RouteScreen.ReportLocation) },
                            reportId = args.reportId
                        )
                    }
                    composable<RouteScreen.SearchContact> {
                        SearchContactScreen(
                            navigateBack = { navController.popBackStack() },
                            onUserSelected = { conversationId, isConversation ->
                                navController.navigate(RouteScreen.Conversation(conversationId, isConversation))
                            },
                        )
                    }
                    composable<RouteScreen.Conversation> { backStackEntry ->
                        val args = backStackEntry.toRoute<RouteScreen.Conversation>()
                        ConversationScreen(
                            id = args.id,
                            isConversation = args.isConversation,
                            navigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}