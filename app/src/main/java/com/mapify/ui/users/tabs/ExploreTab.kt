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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.mapify.model.Report
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing

@Composable
fun ExploreTab(
    navigateToDetail: (String) -> Unit
) {

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel

    LaunchedEffect(Unit) {
        reportsViewModel.restartReportsRealtime()
    }
    DisposableEffect(Unit) {
        onDispose {
            reportsViewModel.resetReportsListener()
        }
    }

    val storedReports by reportsViewModel.reports.collectAsState()
    val visibleReports = storedReports.filter { !it.isDeleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large),
    ) {
        items(visibleReports) {
            ReportCard(
                report = it,
                navigateToDetail = navigateToDetail
            )
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
                            imageVector = if (report.isHighPriority)
                                Icons.Filled.Star
                            else
                                Icons.Filled.StarOutline,
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