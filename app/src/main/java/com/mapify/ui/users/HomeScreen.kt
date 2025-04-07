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
import com.mapify.ui.users.navigation.UserNavigation
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateFAB
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.users.navigation.UserRouteTab

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToCreateReport: () -> Unit,
    navigateToDetail: (String) -> Unit
) {
    //TODO: add logout icon (convenient for tests, anyway)

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.Center,
                stringResource(id = R.string.app_name),
                Icons.Filled.AccountCircle,
                stringResource(id = R.string.name_icon_description),
                { navigateToProfile() },
                true,
                Icons.Filled.Settings,
                stringResource(id = R.string.settings_icon),
                { },
//                true,
//                Icons.Filled.Search,
//                stringResource(id = R.string.messages_icon)
            ) //TODO: add action for settings screen
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
                        { /* navigate to messages list */ },
                        Icons.Filled.Mail,
                        stringResource(id = R.string.messages_icon)
                    )
                }
            }
        }
    ) { padding ->
        UserNavigation(
            padding,
            navController = navController,
            navigateToDetail = navigateToDetail
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

        }
    }
}