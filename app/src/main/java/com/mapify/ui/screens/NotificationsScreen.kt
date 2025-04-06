package com.mapify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.NotificationItem
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navigateToHome: () -> Unit,
    navigateToExplore: () -> Unit,
    navigateToMessages: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val notifications = listOf(
        Triple("Report Rejected", false, "2 min ago"),
        Triple("Report Verified", true, "10 min ago"),
        Triple("Report Rejected", false, "1 hour ago"),
        Triple("Report Verified", true, "2 days ago"),
        Triple("Report Rejected", false, "Just now")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.notifications),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateToProfile()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(id = R.string.name_icon_description),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción futura de configuración */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings_icon)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                notificationsSelected = true,
                navigateToHome = navigateToHome,
                navigateToExplore = navigateToExplore,
                navigateToMessages = navigateToMessages,
                navigateToNotifications = {}
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(notifications) { (message, isVerified, time) ->
                NotificationItem(
                    title = message,
                    status = if (isVerified) stringResource(id = R.string.verified)
                    else stringResource(id = R.string.rejected),
                    supportingText = time,
                    statusColor = if (isVerified)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
