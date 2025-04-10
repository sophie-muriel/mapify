package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.model.*
import com.mapify.ui.components.NotificationItem
import com.mapify.ui.theme.Spacing
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun NotificationsTab(
    navigateToReportView: (String, ReportStatus) -> Unit
) {
    //TODO: report deleted logic

    val storedReports = listOf(
        Report(
            id = "1",
            title = "Report 1",
            category = Category.SECURITY,
            description = "This is a report",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.NOT_VERIFIED,
            userId = "1",
            date = LocalDateTime.now()
        ),
        Report(
            id = "2",
            title = "Report 2",
            category = Category.PETS,
            description = "This is a test report...",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
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
            description = "Another report example",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.VERIFIED,
            userId = "2",
            date = LocalDateTime.now()
        ),
        Report(
            id = "4",
            title = "Report 4",
            category = Category.COMMUNITY,
            description = "Report about illegal dumping near the river.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.VERIFIED,
            userId = "3",
            date = LocalDateTime.now().minusHours(3),
            isResolved = false,
            priorityCounter = 10
        ),
        Report(
            id = "5",
            title = "Report 5",
            category = Category.SECURITY,
            description = "Potholes causing problems in traffic.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.PENDING_VERIFICATION,
            userId = "4",
            date = LocalDateTime.now().minusDays(1),
            isResolved = false,
            priorityCounter = 3
        ),
        Report(
            id = "6",
            title = "Report 6",
            category = Category.PETS,
            description = "Lost dog seen in the neighborhood.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.VERIFIED,
            userId = "5",
            date = LocalDateTime.now().minusMinutes(45),
            isResolved = true,
            priorityCounter = 15
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        items(storedReports) { report ->
            NotificationItem(
                title = report.title,
                status = if (report.status == ReportStatus.VERIFIED)
                    stringResource(id = R.string.verified)
                else
                    stringResource(id = R.string.rejected),
                supportingText = formatNotificationDate(report.date),
                statusMessage = if (report.status == ReportStatus.VERIFIED)
                    "Your report has been verified, congratulations!"
                else
                    "Your report has been rejected; modify it in 3 days or it will be deleted.",
                imageUrl = report.images.first(),
                onClick = { navigateToReportView(report.id, report.status) },
                statusColor = if (report.status == ReportStatus.VERIFIED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

fun formatNotificationDate(date: LocalDateTime): String {
    val now = LocalDate.now()
    val messageDate = date.toLocalDate()
    return when {
        messageDate.isEqual(now) -> {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "CO"))
            date.format(formatter)
        }

        messageDate.isEqual(now.minusDays(1)) -> {
            "Yesterday"
        }

        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "CO"))
            date.format(formatter)
        }
    }
}