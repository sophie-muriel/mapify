package com.mapify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateReportFloatingButton
import com.mapify.ui.components.SimpleTopBar

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit,
    navigateToCreateReport: () -> Unit,
    navigateToExplore: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToMessages: () -> Unit

) {
    //TODO: add logout icon (convenient for tests, anyway)

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
                homeSelected = true,
                navigateToExplore = navigateToExplore,
                navigateToNotifications = navigateToNotifications,
                navigateToMessages = navigateToMessages
            )
        },
        floatingActionButton = {
            CreateReportFloatingButton(navigateToCreateReport = navigateToCreateReport)
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(text = "Content", modifier = Modifier.padding(16.dp))
        }
    }
}