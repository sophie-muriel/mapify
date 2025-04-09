package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.ui.components.SimpleTopBar
import com.mapify.R
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import java.time.LocalDateTime

@Composable
fun ReportViewScreen(
    reportId: String, navigateBack: () -> Unit
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
            date = LocalDateTime.now(),
            priorityCounter = 10
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
            date = LocalDateTime.now(),
            priorityCounter = 11
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
            priorityCounter = 21
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
            isResolved = true,
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

    val report = storedReports.find { it.id == reportId } ?: return
    val starIcon = if (report.isHighPriority) Icons.Filled.Star else Icons.Filled.StarOutline
    val starIconDescription = if (report.isHighPriority)
        stringResource(id = R.string.star_icon_prioritized) else stringResource(id = R.string.star_icon_not_prioritized)
    val tint = if (report.isHighPriority) MaterialTheme.colorScheme.primary else LocalContentColor.current

    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.CenterStart,
                stringResource(id = R.string.report_view),
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
                true,
                firstActionIconVector = starIcon,
                starIconDescription,
                {},
                true,
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more_vertical_dots),
                {},
                tint = tint
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = reportId)

        }
    }
}


