package com.mapify.ui.users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.mapify.R
import com.mapify.ui.users.navigation.UserNavigation
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateReportFloatingButton
import com.mapify.ui.components.SimpleTopBar

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToCreateReport: () -> Unit,
    navigateToDetail: (String) -> Unit
) {
    //TODO: add logout icon (convenient for tests, anyway)

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.Center,
                stringResource(id = R.string.app_name),
                Icons.Filled.AccountCircle,
                stringResource(id = R.string.name_icon_description),
                { navigateToProfile() },
                false
            ) //TODO: add action for settings screen
        }, bottomBar = {
            BottomNavigationBar(
                navController = navController,
            )
        },
        floatingActionButton = {
            CreateReportFloatingButton(navigateToCreateReport = navigateToCreateReport)
        }) { padding ->
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