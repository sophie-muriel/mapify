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
import androidx.compose.material.icons.filled.MoreVert
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
import com.mapify.model.Category
import com.mapify.model.Location
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
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Unpublished
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.mapify.model.Comment
import com.mapify.model.Message
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.MenuAction
import com.mapify.ui.components.MinimalDropdownMenu
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportViewScreen(
    reportId: String,
    reportStatusP: ReportStatus? = null,
    navigateBack: () -> Unit,
    navigateToReportEdit: ((String) -> Unit)? = null,
    navigateToReportLocation: () -> Unit,
    isAdmin: Boolean,
    userId: String
) {
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

    val storedReports = listOf(
        Report(
            id = "1",
            title = "Report 1",
            category = Category.SECURITY,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
                    "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
                    "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit." +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
                    "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. " +
                    "Donec sed pharetra sapien. Nam vitae mi eleifend ex pellentesque vulputate ac in elit.",
            images = listOf(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOvSWqWExnQHszC2ZfSLd-xZNC94pRxMO7ag&s"
            ),
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
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTFSUC03tbmiZ9hVh3ShKNIJmVyPVk4XIf16A&s"
            ),
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
            description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor. " +
                    "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
                    "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
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
            description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.VERIFIED,
            userId = "2",
            date = LocalDateTime.now().minusHours(3),
            priorityCounter = 21
        ),
        Report(
            id = "5",
            title = "Report 5",
            category = Category.SECURITY,
            description = "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
                    "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.PENDING_VERIFICATION,
            userId = "2",
            date = LocalDateTime.now().minusDays(1),
            isResolved = true,
            priorityCounter = 3
        ),
        Report(
            id = "6",
            title = "Report 6",
            category = Category.PETS,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque. " +
                    "Donec et tortor scelerisque, ornare mi et, tempus sem. Maecenas ullamcorper nulla vel arcu malesuada consectetur. ",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
            status = ReportStatus.VERIFIED,
            userId = "2",
            date = LocalDateTime.now().minusMinutes(45),
            isResolved = true,
            priorityCounter = 15
        )
    )

    val storedUsers = listOf(
        User(
            id = "1",
            fullName = "First User",
            email = "first@mail.com",
            password = "ThisIsATestPass",
            role = Role.CLIENT,
            registrationLocation = Location(
                latitude = 43230.1,
                longitude = 753948.7,
                country = "Colombia",
                city = "Armenia"
            )
        ),
        User(
            id = "2",
            fullName = "Second User",
            email = "second@mail.com",
            password = "ThisIsATestPass",
            role = Role.CLIENT,
            registrationLocation = Location(
                latitude = 43230.1,
                longitude = 753948.7,
                country = "Colombia",
                city = "Armenia"
            )
        ),
        User(
            id = "3",
            fullName = "Test commenter",
            email = "second@mail.com",
            password = "ThisIsATestPass",
            role = Role.CLIENT,
            registrationLocation = Location(
                latitude = 43230.1,
                longitude = 753948.7,
                country = "Colombia",
                city = "Armenia"
            )
        )

    )

    var storedComments by remember {
        mutableStateOf(
            listOf<Comment>(
                Comment(
                    id = "1",
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis tempus tellus luctus dictum pellentesque.",
                    userId = "1",
                    reportId = reportId,
                    date = LocalDateTime.now()
                ),
                Comment(
                    id = "2",
                    content = "Lorem ipsum dolor sit amet",
                    userId = "2",
                    reportId = reportId,
                    date = LocalDateTime.now()
                ),
                Comment(
                    id = "3",
                    content = "Lorem ipsum dolor sit amet",
                    userId = "2",
                    reportId = reportId,
                    date = LocalDateTime.now()
                )
            )
        )
    }

    val report = storedReports.find { it.id == reportId } ?: return
    var reportStatus by remember { mutableStateOf(report.status) } //This allows to change verification icon
    //val reportStatus = report.status
    val starIcon = if (report.isHighPriority) Icons.Filled.Star else Icons.Filled.StarOutline
    val starIconDescription = if (report.isHighPriority)
        stringResource(id = R.string.star_icon_prioritized) else stringResource(id = R.string.star_icon_not_prioritized)
    val tint =
        if (report.isHighPriority) MaterialTheme.colorScheme.primary else LocalContentColor.current


    val state = rememberCarouselState { report.images.count() }
    val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    var showComments by remember { mutableStateOf(false) }
    var bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var comment by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var commentCounter by rememberSaveable { mutableIntStateOf(4) }

    var isCreator = userId == report.userId
    var showDeleteDialogVisible by rememberSaveable { mutableStateOf(false) }
    var showVerifyDialogle by rememberSaveable { mutableStateOf(false) }
    var showRejectionInputDialog by rememberSaveable { mutableStateOf(false) }
    var rejectionMessage by remember { mutableStateOf("") }
    var boostCounter by rememberSaveable { mutableIntStateOf(0) } //One to one database table is needed for this
    var showBoostToast by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val menuItems =
        if (isCreator) {
            listOf(
                MenuAction.Simple(
                    stringResource(id = R.string.edit),
                    {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.edit_icon_description),
                        )
                    }
                ) {
                    navigateToReportEdit?.let { it(reportId) }
                },
                MenuAction.Simple(
                    stringResource(id = R.string.delete),
                    {
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
                    stringResource(id = R.string.verify),
                    {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = stringResource(id = R.string.check_circle_icon),
                        )
                    }
                ) {
                    showVerifyDialogle = true
                },
                MenuAction.Simple(
                    stringResource(id = R.string.reject),
                    {
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
                    stringResource(id = R.string.delete),
                    {
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
                    stringResource(id = R.string.boost_priority),
                    {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = stringResource(id = R.string.trending_up_icon),
                        )
                    }
                ) {
                    boostCounter++
                    showBoostToast = true
                }
            )
        }

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
                secondAction = true,
                secondActionContent = {
                    MinimalDropdownMenu(menuItems)
                },
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
                            navigateToReportLocation()
                        },
                        isClickable = true
                    )
                }

                ItemDetailReport(
                    icon = Icons.Default.Person,
                    iconDescription = stringResource(id = R.string.person_icon),
                    text = storedUsers.find { it.id == report.userId }?.fullName ?: ""
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
            Comments(
                state = bottomSheetState,
                onDismissRequest = {
                    showComments = false
                },
                comments = storedComments,
                comment = comment,
                onCommentChange = { comment = it },
                onClick = {
                    var newComment = Comment(
                        id = commentCounter.toString(),
                        content = comment,
                        userId = "3", //ToDo: when we have proper user navigation we can use currentUser.Id
                        reportId = reportId,
                        date = LocalDateTime.now()
                    )
                    commentCounter++
                    storedComments = storedComments + newComment
                    comment = ""
                },
                users = storedUsers,
            )
        }

        val reportDeleted = stringResource(id = R.string.report_deleted)

        if(showDeleteDialogVisible){
            GenericDialog(
                title = stringResource(id = R.string.delete_report_title),
                message = stringResource(id = R.string.delete_report_description),
                onClose = { showDeleteDialogVisible = false },
                onExit = {
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

        if(showVerifyDialogle && reportStatus != ReportStatus.VERIFIED){
            GenericDialog(
                title = stringResource(id = R.string.verify_report_title),
                message = stringResource(id = R.string.verify_report_description),
                onClose = { showVerifyDialogle = false },
                onExit = {
                    Toast.makeText(context, reportVerified, Toast.LENGTH_SHORT).show()
                    showVerifyDialogle = false
                    reportStatus = ReportStatus.VERIFIED
                },
                onCloseText =stringResource(id = R.string.cancel),
                onExitText = stringResource(id = R.string.verify)
            )
        }else if(showVerifyDialogle && reportStatus == ReportStatus.VERIFIED){
            Toast.makeText(context, reportVerifiedMessage, Toast.LENGTH_SHORT).show()
            showVerifyDialogle = false
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
                    reportStatus = ReportStatus.PENDING_VERIFICATION
                    rejectionMessage = ""
                    //TODO: here we will have to create an instance of message and save it in the database
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

        if(showBoostToast && boostCounter == 1){
            Toast.makeText(context, reportBoosted, Toast.LENGTH_SHORT).show()
            showBoostToast = false
        }else if(showBoostToast && boostCounter > 1){
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
                Text(
                    text = text
                )
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
    onClick: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
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
                            if (comment.userId == "3") { //ToDo: when we have proper user navigation we can use currentUser.Id
                                Text(
                                    text = stringResource(id = R.string.me),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    supportingContent = {
                        Text(
                            text = comment.content
                        )
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
                .fillMaxWidth()
                .padding(horizontal = Spacing.Small + Spacing.Sides, vertical = Spacing.Large),
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
                    .height(52.dp)
                    .padding(end = Spacing.Large)
                    .heightIn(min = 52.dp, max = 140.dp),
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


