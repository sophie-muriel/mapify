package com.mapify.ui.users.tabs

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.model.*
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.NotificationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.RequestResultEffectHandler
import com.mapify.utils.SharedPreferencesUtils
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun NotificationsTab(
    navigateToReportView: (String, ReportStatus) -> Unit
) {
    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel

    val storedReports by reportsViewModel.userReports.collectAsState()

    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()

    val context = LocalContext.current
    val userId = SharedPreferencesUtils.getPreference(context)["userId"]

    var currentReportDeletionMessage by rememberSaveable { mutableStateOf("") }

    var isLoading = rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        reportsViewModel.getReportsByUserId(userId ?: "")
    }

    var remainingDays by rememberSaveable { mutableIntStateOf(-1) }

    RequestResultEffectHandler(
        requestResult = reportRequestResult,
        context = context,
        isLoading = isLoading,
        onResetResult = { reportsViewModel.resetReportRequestResult() },
        onNavigate = { },
        showsMessage = false
    )

    if (isLoading.value && storedReports.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
                strokeWidth = 4.dp
            )
        }
        return
    }

    if (storedReports.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_notifications_found),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            items(storedReports) { report ->
                if (report.isDeleted) {
                    report.generateDeletionMessage()
                    currentReportDeletionMessage = report.deletionMessage ?: ""
                    Log.d("NotificationsTab", "Report deletion message: $currentReportDeletionMessage")
                    NotificationItem(
                        title = stringResource(id = R.string.report_deleted),
                        status = stringResource(id = R.string.deleted),
                        supportingText = formatNotificationOrMessageDate(
                            report.lastAdminActionDate ?: LocalDateTime.now()
                        ),
                        statusMessage = report.deletionMessage
                            ?: "", //id = R.string.report_deleted_message
                        onClick = {
                            exitDialogVisible = true
                        },
                        statusColor = MaterialTheme.colorScheme.error
                    )
                } else if (report.status == ReportStatus.PENDING_VERIFICATION) {
                    remainingDays = report.remainingDaysToDeletion
                    NotificationItem(
                        title = report.title,
                        status = stringResource(id = R.string.rejected),
                        supportingText = formatNotificationOrMessageDate(
                            report.lastAdminActionDate ?: LocalDateTime.now()
                        ),
                        statusMessage = stringResource(
                            id = R.string.report_rejected_days_remaining,
                            remainingDays
                        ),
                        imageUrl = report.images.first(),
                        onClick = { navigateToReportView(report.id, report.status) },
                        statusColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    NotificationItem(
                        title = report.title,
                        status = stringResource(id = R.string.verified),
                        supportingText = formatNotificationOrMessageDate(
                            report.lastAdminActionDate ?: LocalDateTime.now()
                        ),
                        statusMessage = stringResource(id = R.string.report_verified_message),
                        imageUrl = report.images.first(),
                        onClick = { navigateToReportView(report.id, report.status) },
                        statusColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (storedReports.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.Large))
                }
            }
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.report_deleted),
            message = currentReportDeletionMessage,
            onExit = {
                exitDialogVisible = false
            },
            onExitText = stringResource(id = R.string.ok)
        )
    }
}

@Composable
fun formatNotificationOrMessageDate(date: LocalDateTime): String {
    val now = LocalDate.now()
    val notificationOrMessageDate = date.toLocalDate()
    return when {
        notificationOrMessageDate.isEqual(now) -> {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "CO"))
            date.format(formatter)
        }

        notificationOrMessageDate.isEqual(now.minusDays(1)) -> {
            stringResource(id = R.string.yesterday)
        }

        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "CO"))
            date.format(formatter)
        }
    }
}