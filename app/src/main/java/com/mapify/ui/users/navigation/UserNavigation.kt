package com.mapify.ui.users.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mapify.model.ReportStatus
import com.mapify.ui.navigation.RouteScreen
import com.mapify.ui.users.tabs.ExploreTab
import com.mapify.ui.users.tabs.HomeTab
import com.mapify.ui.users.tabs.NotificationsTab
import com.mapify.ui.users.tabs.ProfileTab

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun UserNavigation(
    padding: PaddingValues,
    navController: NavHostController,
    navigateToDetail: (String) -> Unit,
    navigateToReportView: (String, ReportStatus) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = UserRouteTab.Home
    ) {
        composable<UserRouteTab.Home> {
            HomeTab(
                navigateToDetail = navigateToDetail
            )
        }
        composable<UserRouteTab.Explore> {
            ExploreTab(
                navigateToDetail = navigateToDetail
            )
        }
        composable<UserRouteTab.Notifications> {
            NotificationsTab(
                navigateToReportView = navigateToReportView
            )
        }
        composable<UserRouteTab.Profile> {
            ProfileTab()
        }
    }
}