package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.model.*
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.NotificationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun NotificationsTab(
    navigateToReportView: (String, ReportStatus) -> Unit
) {
    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel

    val storedReports by reportsViewModel.reports.collectAsState()

    val context = LocalContext.current

    LocalMainViewModel.current.usersViewModel.loadUser(context)
    val user = LocalMainViewModel.current.usersViewModel.user.value ?: return

    val userReports = storedReports.filter { it.userId == user.id }.sortedByDescending { it.date }

    var remainingDays = -1

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        items(userReports) { report ->
            if(report.userId == user.id){
                if(report.isDeleted){
                    NotificationItem(
                        title = stringResource(id = R.string.report_deleted),
                        status = stringResource(id = R.string.deleted),
                        supportingText = formatNotificationOrMessageDate(LocalDateTime.now()),
                        statusMessage = stringResource(id = R.string.report_deleted_message),
                        onClick = {
                            exitDialogVisible = true
                        },
                        statusColor = MaterialTheme.colorScheme.error
                    )
                }else{
                    remainingDays = report.remainingDaysToDeletion
                    NotificationItem(
                        title = report.title,
                        status = if (report.status == ReportStatus.VERIFIED)
                            stringResource(id = R.string.verified)
                        else
                            stringResource(id = R.string.rejected),
                        supportingText = formatNotificationOrMessageDate(report.date),
                        statusMessage = if (report.status == ReportStatus.VERIFIED)
                            stringResource(id = R.string.report_verified_message)
                        else
                            stringResource(id = R.string.report_rejected_days_remaining, remainingDays),
                        imageUrl = report.images.first(),
                        onClick = { navigateToReportView(report.id, report.status) },
                        statusColor = if (report.status == ReportStatus.VERIFIED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        if (userReports.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(Spacing.Large))
            }
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.report_deleted),
            message = stringResource(id = R.string.report_deleted_message),
            onExit = {
                exitDialogVisible = false
            },
            onExitText = stringResource(id = R.string.ok)
        )
    }
//    HandleLocationPermission(
//        onPermissionGranted = {
//
//        }
//    )

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