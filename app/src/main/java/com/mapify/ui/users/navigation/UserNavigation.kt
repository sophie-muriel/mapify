package com.mapify.ui.users.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mapify.model.ReportStatus
import com.mapify.ui.navigation.RouteScreen
import com.mapify.ui.screens.SearchContactScreen
import com.mapify.ui.users.tabs.ExploreTab
import com.mapify.ui.users.tabs.HomeTab
import com.mapify.ui.users.tabs.MessagesTab
import com.mapify.ui.users.tabs.NotificationsTab
import com.mapify.viewmodel.UsersViewModel

@Composable
fun UserNavigation(
    padding: PaddingValues,
    navController: NavHostController,
    navigateToDetail: (String) -> Unit,
    navigateToReportView: (String, ReportStatus) -> Unit,
    navigateToConversation: (String, Boolean) -> Unit,
    usersViewModel: UsersViewModel
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
        composable<UserRouteTab.Messages> {
            MessagesTab(
                navigateToConversation = navigateToConversation,
                usersViewModel = usersViewModel
            )
        }
        composable("SearchContact") {
            SearchContactScreen(
                navigateBack = { navController.popBackStack() },
                onUserSelected = { id, isConversation ->
                    navController.navigate(RouteScreen.Conversation(id, isConversation))
                },
                usersViewModel = usersViewModel
            )
        }
    }
}