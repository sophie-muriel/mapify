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
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.NotificationItem
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navigateToHome: () -> Unit,
    navigateToExplore: () -> Unit,
    navigateToMessages: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToReportView: (String) -> Unit
) {
    val storedReports = listOf(
        Report(
            id = "1",
            title = "Report 1",
            category = Category.SECURITY,
            description = "This is a report",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s"),
            location = Location(
                latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
            ),
            status = ReportStatus.NOT_VERIFIED,
            userId = "1",
            date = LocalDateTime.now()
        ),
        Report(
            id = "2",
            title = "Report 2",
            category = Category.PETS,
            description = "This is an embedded test report to test the pets category and the resolved flag and verified status",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s"),
            location = Location(
                latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
            ),
            status = ReportStatus.VERIFIED,
            userId = "1",
            date = LocalDateTime.now(),
            isResolved = true,
            priorityCounter = 25
        ),
        Report(
            id = "3",
            title = "Report 3",
            category = Category.INFRASTRUCTURE,
            description = "This is an embedded test report",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(
                latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
            ),
            status = ReportStatus.VERIFIED,
            userId = "2",
            date = LocalDateTime.now()
        ),
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
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.error
                )}
        }
    }
}
