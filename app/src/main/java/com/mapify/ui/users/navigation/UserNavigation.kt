package com.mapify.ui.users.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mapify.ui.users.tabs.ExploreTab
import com.mapify.ui.users.tabs.HomeTab
import com.mapify.ui.users.tabs.MessagesTab
import com.mapify.ui.users.tabs.NotificationsTab

@Composable
fun UserNavigation(
    padding: PaddingValues,
    navController: NavHostController,
    navigateToDetail: (String) -> Unit
) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = UserRouteTab.Home
    ) {
        composable<UserRouteTab.Home> { HomeTab() }
        composable<UserRouteTab.Explore> { ExploreTab(navigateToDetail = navigateToDetail) }
        composable<UserRouteTab.Notifications> { NotificationsTab() }
        composable<UserRouteTab.Messages> { MessagesTab() }
    }
}