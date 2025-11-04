package com.mapify.ui.users.tabs

import DistanceCalculator
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.mapify.R
import com.mapify.model.Report
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ExploreTab(
    navigateToDetail: (String) -> Unit
) {

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val context = LocalContext.current
    val reports by reportsViewModel.reports.collectAsState()

    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED)
    }

    LaunchedEffect(Unit) {
        reportsViewModel.getReports()
    }

    val filteredReports by reportsViewModel.filteredReports.collectAsState()
    val searchFilters by reportsViewModel.searchFilters.collectAsState()
    var reportsToDisplay by rememberSaveable { mutableStateOf(reports) }
    var isPopUpVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(searchFilters, reports) {
        isPopUpVisible = searchFilters.areSet
        if (searchFilters.areSet) {
            reportsToDisplay = filteredReports
            if (searchFilters.onlyThisDistance) {
                reportsToDisplay = reportsViewModel.reportsWithinDistance(
                    reports = reportsToDisplay,
                    context = context,
                    filterDistance = searchFilters.thisDistance
                )
            }
        }else {
            reportsToDisplay = reports
        }

        if (searchFilters.isJustDistance) {
            reportsToDisplay = reportsViewModel.reportsWithinDistance(
                reports = reportsToDisplay,
                context = context,
                filterDistance = searchFilters.thisDistance
            )
        }
    }

    if (isPopUpVisible) {
        if (reportsToDisplay.isEmpty()){
            GenericDialog(
                title = stringResource(id = R.string.no_reports_found),
                message = stringResource(id = R.string.no_reports_found_message),
                onExit = {
                    isPopUpVisible = false
                },
                onExitText = "Ok"
            )
        }
    }

    if (reportsToDisplay.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_reports_found),
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
            verticalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            items(reportsToDisplay) {
                val individualReportDistance = remember { mutableDoubleStateOf(0.0) }
                DistanceCalculator(
                    context = context,
                    report = it,
                    distance = individualReportDistance
                )
                val formattedDistance = "%.1f".format(individualReportDistance.value)
                ReportCard(
                    report = it,
                    navigateToDetail = navigateToDetail,
                    formattedDistance = formattedDistance
                )
            }
        }
    }
}

@Composable
fun ReportCard(
    report: Report,
    navigateToDetail: (String) -> Unit,
    formattedDistance: String
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
                    contentScale = ContentScale.Crop
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
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(report.category.displayName)
                        }

                        append(" \u2022 ")
                        append(stringResource(id = R.string.km_away, formattedDistance))
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
