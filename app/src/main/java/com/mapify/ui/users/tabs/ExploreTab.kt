package com.mapify.ui.users.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mapify.R
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime

@Composable
fun ExploreTab(
    navigateToDetail: (String) -> Unit
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Inline),
    ) {
        items(storedReports) {
            if(!it.isDeleted){
                ReportCard(
                    report = it,
                    navigateToDetail = navigateToDetail
                )
            }
        }
    }
}

@Composable
fun ReportCard(
    report: Report,
    navigateToDetail: (String) -> Unit
) {
    ElevatedCard(
        onClick = {
            navigateToDetail(report.id)
        }, modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .width(100.dp)
                    .height(100.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = report.images[0],
                    contentDescription = stringResource(id = R.string.report_image),
                    contentScale = ContentScale.FillBounds
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.Small * 3),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Small),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = report.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = if (report.isHighPriority) Icons.Filled.Star else Icons.Filled.StarOutline,
                            contentDescription = stringResource(id = R.string.star_icon),
                            modifier = Modifier
                                .padding(end = Spacing.Small * 3)
                                .size(20.dp),
                            tint = if (report.priorityCounter > 20)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.Small))

                    val statusText = buildAnnotatedString {
                        if (report.isResolved) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(stringResource(id = R.string.resolved))
                            }
                            append(" \u2022 ")
                        }
                        append(report.category.displayName)
                        append(" \u2022 ")
                        append("1.2 KM away") // TODO: Replace with actual distance
                    }

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}