package com.mapify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateReportFloatingButton
import java.time.LocalDateTime
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navigateToCreateReport: () -> Unit,
    navigateToReportView: (String) -> Unit,
    navigateToSearchFilters: () -> Unit,
    navigateToSettings: () -> Unit
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

    Scaffold(topBar = {
        TopNavigationBar2(
            onClickBackArrow = navigateToCreateReport,//TODO: Replace with actual back arrow
            onClickSearch = navigateToSearchFilters,
            onClickSettings = navigateToSettings,
        )
    }, bottomBar = {
        BottomNavigationBar(searchSelected = true)
    }, floatingActionButton = {
        CreateReportFloatingButton(
            navigateToCreateReport = {
                navigateToCreateReport()
            })
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.Sides),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large),
            ) {
                items(storedReports) { report ->
                    ReportCard(
                        report = report, navigateToReportView = navigateToReportView
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar2(
    onClickBackArrow: () -> Unit,
    onClickSearch: () -> Unit,
    onClickSettings: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = Spacing.Small),
        title = {
        Text(
            text = stringResource(id = R.string.explore_screen)
        )
    }, navigationIcon = {
        IconButton(onClick = onClickBackArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back_arrow_icon)
            )
        }
    }, actions = {
        IconButton(onClick = onClickSearch) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(id = R.string.settings_icon)
            )
        }

        IconButton(onClick = onClickSettings) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = stringResource(id = R.string.settings_icon)
            )
        }
    })
}

@Composable
fun ReportCard(
    report: Report, navigateToReportView: (String) -> Unit
) {
    ElevatedCard(
        onClick = {
            navigateToReportView(report.id)
        }, modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                model = report.images[0],
                contentDescription = stringResource(id = R.string.report_image)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small),
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(Spacing.Small))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Inline)
                ) {
                    if (report.isResolved) {
                        Text(
                            text = stringResource(id = R.string.resolved),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(
                            modifier = Modifier
                                .width(3.dp)
                                .height(3.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondary, shape = CircleShape
                                )
                                .align(alignment = Alignment.CenterVertically)
                        )
                    }
                    Text(
                        text = report.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Spacer(
                        modifier = Modifier
                            .width(3.5.dp)
                            .height(3.5.dp)
                            .background(
                                color = Color.Black, shape = CircleShape
                            )
                            .align(alignment = Alignment.CenterVertically)
                    )

                    Text(
                        text = "1.2 KM away", //TODO: Replace with actual distance to user
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = if (report.description.length > 30) report.description.substring(
                        0, 30
                    ) + "..."
                    else report.description.substring(0, report.description.length),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 12.dp, bottom = 12.dp),
            ) {
                Icon(
                    imageVector = if (report.isHighPriority) Icons.Filled.Star else Icons.Filled.StarOutline,
                    contentDescription = stringResource(id = R.string.star_icon),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp),
                    tint = if (report.priorityCounter > 20) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

}