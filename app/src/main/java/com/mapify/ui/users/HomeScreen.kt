package com.mapify.ui.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapify.R
import com.mapify.model.Conversation
import com.mapify.model.ReportStatus
import com.mapify.ui.users.navigation.UserNavigation
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateFAB
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.users.navigation.UserRouteTab

@Composable
fun HomeScreen(
    isAdmin: Boolean,
    userId: String,
    navigateToProfile: () -> Unit,
    navigateToCreateReport: () -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToConversation: (String) -> Unit,
    navigateToReportView: (String, ReportStatus) -> Unit,
    navigateToSearchFilters: () -> Unit,
    navigateToSearchContact: () -> Unit
) {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    val alignment = Alignment.Center
    val navIconVector = Icons.Filled.AccountCircle
    val navIconDescription = stringResource(id = R.string.name_icon_description)
    val onClickNavIcon = { navigateToProfile() }
    val actions = true
    val settingsIconVector = Icons.Filled.Settings
    val settingsIconDescription = stringResource(id = R.string.settings_icon)

    Scaffold(
        topBar = {
            when (currentRoute) {
                UserRouteTab.Home::class.qualifiedName -> {
                    SimpleTopBar(alignment, stringResource(id = R.string.app_name),
                        navIconVector, navIconDescription, onClickNavIcon, actions,
                        settingsIconVector, settingsIconDescription, firstOnClickAction = { navigateToSettings() }
                    )
                }
                UserRouteTab.Explore::class.qualifiedName -> {
                    SimpleTopBar(alignment, stringResource(id = R.string.explore_screen),
                        navIconVector, navIconDescription, onClickNavIcon, actions,
                        Icons.Filled.Search, stringResource(id = R.string.search_icon),
                        { navigateToSearchFilters() }, true,settingsIconVector, settingsIconDescription, secondOnClickAction = { navigateToSettings() }
                    )
                }
                UserRouteTab.Notifications::class.qualifiedName -> {
                    SimpleTopBar(alignment, stringResource(id = R.string.notifications),
                        navIconVector, navIconDescription, onClickNavIcon, actions,
                        settingsIconVector, settingsIconDescription, firstOnClickAction = { navigateToSettings() }
                    )
                }
                UserRouteTab.Messages::class.qualifiedName -> {
                    SimpleTopBar(alignment, stringResource(id = R.string.messages_label),
                        navIconVector, navIconDescription, onClickNavIcon, actions,
                        settingsIconVector, settingsIconDescription, firstOnClickAction = { navigateToSettings() }
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
            )
        },
        floatingActionButton = {
            when (currentRoute) {
                UserRouteTab.Home::class.qualifiedName, UserRouteTab.Explore::class.qualifiedName -> {
                    CreateFAB(
                        { navigateToCreateReport() },
                        Icons.Filled.Add,
                        stringResource(id = R.string.add_icon_description)
                    )
                }
                UserRouteTab.Messages::class.qualifiedName -> {
                    CreateFAB(
                        { navigateToSearchContact() },
                        Icons.Filled.Mail,
                        stringResource(id = R.string.messages_icon)
                    )
                }
            }
        }
    ) { padding ->
        UserNavigation(
            padding = padding,
            navController = navController,
            navigateToDetail = navigateToDetail,
            isAdmin = isAdmin,
            navigateToReportView = navigateToReportView,
            navigateToConversation = navigateToConversation
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}