package com.mapify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.IndeterminateCheckBox
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mapify.ui.components.SimpleTopBar
import com.mapify.R
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
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
            images = listOf(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOvSWqWExnQHszC2ZfSLd-xZNC94pRxMO7ag&s"),
            location = Location(
                latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
            ),
            status = ReportStatus.PENDING_VERIFICATION,
            userId = "1",
            date = LocalDateTime.now(),
            priorityCounter = 10
        ),
        Report(
            id = "2",
            title = "Report 2",
            category = Category.PETS,
            description = "This is an embedded test report to test the pets category and the resolved flag and verified status",
            images = listOf(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSHtshKCjboh0e9X3dP5l-igYWWA4C8-nSaw&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcThXTf5MoQt2F4rJ9lnIRpA-fQ7zZNSRQwtkQ&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFSUC03tbmiZ9hVh3ShKNIJmVyPVk4XIf16A&s"),
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
    val reportStatus = report.status

    val state = rememberCarouselState { report.images.count() }

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
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            Carousel(
                state = state,
                list = report.images
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.Sides)
                    .align(Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large)
            ){
                TitleAndVerified(
                    report = report,
                    reportStatus = reportStatus
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Inline),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip(
                        icon = Icons.Outlined.Sell,
                        text = report.category.displayName,
                        onClick = {  }
                    )
                    InfoChip(
                        icon = Icons.Default.Place,
                        text = "1.2KM away",
                        onClick = {
                            //navigate to mapView }
                        },
                        isClickable = true
                    )
                }
            }
        }
    }
}

@Composable
fun TitleAndVerified(
    report: Report,
    reportStatus: ReportStatus
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.Inline),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = report.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Icon(
            imageVector = when (reportStatus) {
                ReportStatus.VERIFIED -> Icons.Filled.CheckBox
                ReportStatus.NOT_VERIFIED -> Icons.Outlined.IndeterminateCheckBox
                else -> Icons.Filled.IndeterminateCheckBox
            },
            contentDescription = stringResource(id = R.string.star_icon),
            tint = when (reportStatus) {
                ReportStatus.VERIFIED -> MaterialTheme.colorScheme.primary
                ReportStatus.NOT_VERIFIED -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carousel(
    state: CarouselState,
    list: List<String>
){
    HorizontalUncontainedCarousel(
        state = state,
        modifier = Modifier
            .width(412.dp)
            .height(250.dp),
        itemWidth = 248.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = Spacing.Large),
    ) {
        val item = list[it]
        AsyncImage(
            modifier = Modifier
                .height(248.dp)
                .aspectRatio(1f)
                .maskClip(MaterialTheme.shapes.extraLarge),
            model = item,
            contentDescription = stringResource(id = R.string.report_image),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun InfoChip(
    icon: ImageVector,
    text: String,
    isClickable: Boolean = false,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.then(
            if (isClickable) {
                Modifier.clickable(onClick = onClick)
            } else {
                Modifier
            }
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

}


