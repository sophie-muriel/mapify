package com.mapify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.IndeterminateCheckBox
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mapify.ui.components.SimpleTopBar
import com.mapify.R
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.Role
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime
import com.mapify.model.User
import com.mapify.ui.components.CreateFAB
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unpublished
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.mapify.model.Comment
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.MenuAction
import com.mapify.ui.components.MinimalDropdownMenu
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.SharedPreferencesUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportViewScreen(
    reportId: String,
    reportStatusP: ReportStatus? = null,
    navigateBack: () -> Unit,
    navigateToReportEdit: ((String) -> Unit)? = null,
    navigateToReportLocation: (Double?, Double?) -> Unit,
) {

    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val userId = SharedPreferencesUtils.getPreference(context)["userId"]

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel

    val storedReports by reportsViewModel.reports.collectAsState()

    val users by usersViewModel.users.collectAsState()

    val isAdmin = SharedPreferencesUtils.getPreference(context)["role"] == Role.ADMIN.toString()

    if (reportStatusP != null && navigateToReportEdit != null) {
        var exitDialogVisible by rememberSaveable { mutableStateOf(true) }

        if (exitDialogVisible && reportStatusP == ReportStatus.PENDING_VERIFICATION) {
            GenericDialog(
                title = stringResource(id = R.string.report_rejected),
                message = stringResource(id = R.string.report_rejected_message),
                onClose = {
                    exitDialogVisible = false
                },
                onExit = {
                    navigateToReportEdit(reportId)
                },
                onCloseText = stringResource(id = R.string.cancel),
                onExitText = stringResource(id = R.string.edit_report_now)
            )
        }
    }

    var storedComments by remember { mutableStateOf(emptyList<Comment>()) }

    val report = storedReports.find { it.id == reportId } ?: return
    var reportStatus by remember { mutableStateOf(report.status) }

    val starIcon = if (report.isHighPriority) Icons.Filled.Star else Icons.Filled.StarOutline
    val starIconDescription = if (report.isHighPriority)
        stringResource(id = R.string.star_icon_prioritized) else stringResource(id = R.string.star_icon_not_prioritized)
    val tint =
        if (report.isHighPriority) MaterialTheme.colorScheme.primary else LocalContentColor.current


    val state = rememberCarouselState { report.images.count() }
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var showComments by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var comment by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val isCreator = userId == report.userId
    var showDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    var showVerifyDialog by rememberSaveable { mutableStateOf(false) }
    var showRejectionInputDialog by rememberSaveable { mutableStateOf(false) }
    var rejectionMessage by remember { mutableStateOf("") }
    var canBoost by rememberSaveable { mutableStateOf(false) }
    if(userId !in report.reportBoosters){
        canBoost = true
    }

    var boosted by rememberSaveable { mutableStateOf(false) }
    var showBoostToast by rememberSaveable { mutableStateOf(false) }

    val menuItems =
        if (isCreator) {
            listOf(
                MenuAction.Simple(
                    label = stringResource(id = R.string.edit),
                    icon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit_icon_description),
                        )
                    }
                ) {
                    navigateToReportEdit?.let { it(reportId) }
                },
                MenuAction.Simple(
                    label = stringResource(id = R.string.delete),
                    icon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_icon),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                ) {
                    showDeleteDialogVisible = true
                }
            )
        } else if (isAdmin) {
            listOf(
                MenuAction.Simple(
                    label = stringResource(id = R.string.verify),
                    icon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = stringResource(id = R.string.check_circle_icon),
                        )
                    }
                ) {
                    showVerifyDialog = true
                },
                MenuAction.Simple(
                    label = stringResource(id = R.string.reject),
                    icon = {
                        Icon(
                            Icons.Default.Unpublished,
                            contentDescription = stringResource(id = R.string.unpublished_icon),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                ) {
                    showRejectionInputDialog = true
                },
                MenuAction.Simple(
                    label = stringResource(id = R.string.delete),
                    icon = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_icon),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                ) {
                    showDeleteDialogVisible = true
                }
            )
        } else {
            listOf(
                MenuAction.Simple(
                    label = stringResource(id = R.string.boost_priority),
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = stringResource(id = R.string.trending_up_icon),
                        )
                    }
                ) {
                    showBoostToast = true
                }
            )
        }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.report_view),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
                actions = true,
                firstActionIconVector = starIcon,
                firstActionIconDescription = starIconDescription,
                firstOnClickAction = {},
                secondAction = true,
                secondActionContent = { MinimalDropdownMenu(menuItems) },
                tint = tint
            )
        },
        floatingActionButton = {
            CreateFAB(
                onClick = { showComments = true },
                icon = Icons.AutoMirrored.Default.Comment,
                iconDescription = stringResource(id = R.string.comment_icon)
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
                verticalArrangement = Arrangement.spacedBy(Spacing.Sides)
            ) {
                TitleAndVerified(
                    report = report,
                    reportStatus = reportStatus
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Inline),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (report.isResolved) {
                        InfoChip(
                            icon = Icons.Outlined.Check,
                            text = stringResource(id = R.string.resolved),
                            onClick = { }
                        )
                    }
                    InfoChip(
                        icon = Icons.Outlined.Sell,
                        text = report.category.displayName,
                        onClick = { }
                    )
                    InfoChip(
                        icon = Icons.Default.Place,
                        text = "1.2KM",
                        onClick = {
                            navigateToReportLocation(report.location?.latitude, report.location?.longitude)
                        },
                        isClickable = true
                    )
                }

                ItemDetailReport(
                    icon = Icons.Default.Person,
                    iconDescription = stringResource(id = R.string.person_icon),
                    text = users.find { it.id == report.userId }?.fullName ?: ""
                )

                ItemDetailReport(
                    icon = Icons.Filled.Today,
                    iconDescription = stringResource(id = R.string.date_icon),
                    text = report.date.format(dateFormat)
                )

                DescriptionText(
                    text = report.description,
                    scrollState = scrollState
                )
            }
        }

        if (showComments) {
            storedComments = reportsViewModel.findById(reportId)?.comments ?: emptyList()
            Comments(
                state = bottomSheetState,
                onDismissRequest = {
                    showComments = false
                },
                comments = storedComments,
                comment = comment,
                onCommentChange = { comment = it },
                onClick = {
                    val newComment = Comment(
                        id = (reportsViewModel.countComments(reportId) + 1).toString(),
                        content = comment,
                        userId = userId?: "",
                        date = LocalDateTime.now()
                    )
                    report.comments.add(newComment)
                    reportsViewModel.update(report)
                    comment = ""
                },
                users = users,
                userId = userId?: ""
            )
        }

        val reportDeleted = stringResource(id = R.string.report_deleted)

        if(showDeleteDialogVisible){
            GenericDialog(
                title = if(isCreator)
                    stringResource(id = R.string.delete_report_title, stringResource(id = R.string.your))
                else
                    stringResource(id = R.string.delete_report_title, stringResource(id = R.string.this_)),
                message = stringResource(id = R.string.delete_report_description),
                onClose = { showDeleteDialogVisible = false },
                onExit = {
                    reportsViewModel.deactivate(report)
                    Toast.makeText(context, reportDeleted, Toast.LENGTH_SHORT).show()
                    showDeleteDialogVisible = false
                    navigateBack()
                },
                onCloseText = stringResource(id = R.string.cancel),
                onExitText = stringResource(id = R.string.delete)
            )
        }

        val reportVerified = stringResource(id = R.string.report_verified)
        val reportVerifiedMessage = stringResource(id = R.string.report_already_verified)

        if(showVerifyDialog && reportStatus != ReportStatus.VERIFIED){
            GenericDialog(
                title = stringResource(id = R.string.verify_report_title),
                message = stringResource(id = R.string.verify_report_description),
                onClose = { showVerifyDialog = false },
                onExit = {
                    Toast.makeText(context, reportVerified, Toast.LENGTH_SHORT).show()
                    showVerifyDialog = false
                    if(report.rejectionMessage != null){
                        report.rejectionMessage = null
                        report.rejectionDate = null
                    }
                    reportStatus = ReportStatus.VERIFIED
                    report.status = ReportStatus.VERIFIED
                    reportsViewModel.update(report)
                },
                onCloseText =stringResource(id = R.string.cancel),
                onExitText = stringResource(id = R.string.verify)
            )
        }else if(showVerifyDialog && reportStatus == ReportStatus.VERIFIED){
            Toast.makeText(context, reportVerifiedMessage, Toast.LENGTH_SHORT).show()
            showVerifyDialog = false
        }

        val rejectionMessageSend = stringResource(id = R.string.rejection_message_send)
        val rejectionMessageToast = stringResource(id = R.string.ten_characters_message)
        val reportAlreadyRejected = stringResource(id = R.string.report_already_rejected)

        if(showRejectionInputDialog && reportStatus != ReportStatus.PENDING_VERIFICATION){
            GenericDialog(
                title = stringResource(id = R.string.reject_report_title),
                message = stringResource(id = R.string.reject_report_description),
                onClose = { showRejectionInputDialog = false },
                onExit = {
                    if(rejectionMessage.isBlank() || rejectionMessage.length < 10){
                        Toast.makeText(context, rejectionMessageToast, Toast.LENGTH_SHORT).show()
                        return@GenericDialog
                    }
                    Toast.makeText(context, rejectionMessageSend, Toast.LENGTH_SHORT).show()
                    showRejectionInputDialog = false
                    report.rejectionMessage = rejectionMessage
                    reportStatus = ReportStatus.PENDING_VERIFICATION
                    report.status = ReportStatus.PENDING_VERIFICATION
                    report.rejectionDate = LocalDateTime.now()
                    reportsViewModel.update(report)
                    rejectionMessage = ""
                },
                onCloseText =stringResource(id = R.string.cancel),
                onExitText = stringResource(id = R.string.send),
                textField = {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = Spacing.Sides),
                        value = rejectionMessage,
                        onValueChange = {
                            rejectionMessage = it
                        },
                    )
                }
            )
        }else if(showRejectionInputDialog && reportStatus == ReportStatus.PENDING_VERIFICATION){
            Toast.makeText(context, reportAlreadyRejected, Toast.LENGTH_SHORT).show()
            showRejectionInputDialog = false
        }

        val reportBoosted = stringResource(id = R.string.report_boosted)
        val reportAlreadyBoosted = stringResource(id = R.string.report_already_boosted)

        if(showBoostToast && canBoost && !boosted){
            Toast.makeText(context, reportBoosted, Toast.LENGTH_SHORT).show()
            report.priorityCounter++
            report.reportBoosters.add(userId?: "")
            reportsViewModel.update(report)
            boosted = true
            showBoostToast = false
        }else if(showBoostToast && (!canBoost || boosted)){
            Toast.makeText(context, reportAlreadyBoosted, Toast.LENGTH_SHORT).show()
            showBoostToast = false
        }
    }
}

@Composable
fun DescriptionText(
    text: String,
    scrollState: ScrollState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .verticalScroll(scrollState)
        ) {
            Row {
                Text(text = text)
            }
        }

        val showTopGradient = remember { derivedStateOf { scrollState.value > 0 } }
        val showGradient = remember { derivedStateOf { scrollState.value < scrollState.maxValue } }

        if (showTopGradient.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        if (showGradient.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    state: SheetState,
    onDismissRequest: () -> Unit,
    comments: List<Comment>,
    users: List<User>,
    comment: String,
    onCommentChange: (String) -> Unit,
    onClick: () -> Unit,
    userId: String
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 72.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(comments) { comment ->
                        ListItem(
                            headlineContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.Inline)

                                ) {
                                    Text(
                                        text = users.find { it.id == comment.userId }?.fullName ?: "",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    if (comment.userId == userId) {
                                        Text(
                                            text = stringResource(id = R.string.me),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            supportingContent = {
                                Text(text = comment.content)
                            },
                            leadingContent = {
                                Icon(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp),
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = stringResource(id = R.string.person_icon)
                                )
                            },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(vertical = Spacing.Large)
                ) {
                    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .drawBehind {
                                drawLine(
                                    color = borderColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            .padding(top = Spacing.Inline + Spacing.Small)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.Sides),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = comment,
                                onValueChange = onCommentChange,
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.leave_a_comment),
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .heightIn(min = 52.dp, max = 140.dp)
                                    .padding(end = Spacing.Large),
                                maxLines = 4,
                                singleLine = false,
                                shape = MaterialTheme.shapes.large,
                                textStyle = MaterialTheme.typography.bodyMedium,
                            )
                            IconButton(
                                onClick = { onClick() },
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = stringResource(id = R.string.send_icon),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ItemDetailReport(
    icon: ImageVector,
    iconDescription: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Inline)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

@Composable
fun TitleAndVerified(
    report: Report,
    reportStatus: ReportStatus
) {
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
) {
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


