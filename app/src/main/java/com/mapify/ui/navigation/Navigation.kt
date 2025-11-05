package com.mapify.ui.navigation

import LocationPermissionWrapper
import android.os.Build
import android.util.Log
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
import com.mapify.ui.screens.CreateReportScreen
import com.mapify.ui.screens.EditReportScreen
import com.mapify.ui.screens.LoginScreen
import com.mapify.ui.screens.RegistrationScreen
import com.mapify.ui.screens.ReportLocationScreen
import com.mapify.ui.screens.ReportViewScreen
import com.mapify.ui.screens.SettingsScreen
import com.mapify.ui.screens.SearchFiltersScreen
import com.mapify.ui.users.HomeScreen
import com.mapify.utils.SharedPreferencesUtils
import com.mapify.viewmodel.MainViewModel

val LocalMainViewModel =
    staticCompositionLocalOf<MainViewModel> { error("MainViewModel not found!") }

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Navigation(
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val map = SharedPreferencesUtils.getPreference(context)

    Log.d("ROLE viewmodel", map["role"].toString())

    val startDestination = if (map.isNotEmpty()) RouteScreen.Home() else RouteScreen.Login

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
                                mainViewModel.usersViewModel.user.value?.let { currentUser ->
                                    SharedPreferencesUtils.savePreference(
                                        context,
                                        currentUser.id,
                                        currentUser.role
                                    )
                                    navController.navigate(RouteScreen.Home) {
                                        popUpTo(RouteScreen.Login) { inclusive = true }
                                    }
                                } ?: Log.w("Login", "User is null, not navigating")
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
                        val args = it.toRoute<RouteScreen.Home>()
                        HomeScreen(
                            initialSelectedTab = args.selectedTab,
                            navigateToCreateReport = {
                                navController.navigate(RouteScreen.CreateReport())
                            },
                            navigateToDetail = {
                                navController.navigate(RouteScreen.ReportView(it))
                            },
                            navigateToSettings = {
                                navController.navigate(RouteScreen.Settings)
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
                                        longitude = longitude.value,
                                        isCenteredOnUser = latitude.value == null && longitude.value == null,
                                        hasPrimaryFab = false,
                                        backIcon = false
                                    )
                                )
                            },
                            navigateToReportView = {
                                latitude.value = null
                                longitude.value = null
                                navController.popBackStack()
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
                            },
                            isReadOnly = args.isReadOnly,
                            isCenteredOnUser = args.isCenteredOnUser,
                            hasPrimaryFab = args.hasPrimaryFab,
                            backIcon = args.backIcon
                        )
                    }
                    composable<RouteScreen.ReportView> { it ->
                        val args = it.toRoute<RouteScreen.ReportView>()
                        ReportViewScreen(
                            reportId = args.reportId,
                            reportStatusP = args.reportStatus,
                            navigateBack = {
                                latitude.value = null
                                longitude.value = null
                                navController.popBackStack()
                            },
                            navigateToReportEdit = {
                                navController.navigate(RouteScreen.EditReport(it))
                            },
                            navigateToReportLocation = { lat, long ->
                                navController.navigate(
                                    RouteScreen.ReportLocation(
                                        latitude = lat,
                                        longitude = long,
                                        isReadOnly = true,
                                        backIcon = true,
                                    )
                                )
                            }
                        )
                    }
                    composable<RouteScreen.Settings> {
                        SettingsScreen(
                            navigateBack = { navController.popBackStack() },
                            navigateToProfile = {
                                navController.navigate(RouteScreen.Home(selectedTab = 3)) {
                                    popUpTo<RouteScreen.Home> { inclusive = true }
                                }
                            },
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
                                navController.popBackStack()
                            },
                            navigateToReportLocation = { lat, long ->
                                navController.navigate(
                                    RouteScreen.ReportLocation(
                                        latitude = lat,
                                        longitude = long,
                                        backIcon = false
                                    )
                                )
                            },
                            reportId = args.reportId
                        )
                    }
                }
            }
        }
    }
}