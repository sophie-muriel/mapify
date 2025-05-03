package com.mapify.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.model.*
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.ReportForm
import com.mapify.ui.components.SimpleTopBar
import com.mapify.utils.isImageValid
import getLocationName
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import androidx.compose.runtime.saveable.rememberSaveable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EditReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: () -> Unit,
    reportId: String,
    latitude: Double? = null,
    longitude: Double? = null
) {
    val context = LocalContext.current
    var isValidating by remember { mutableStateOf(false) }

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
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
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
            description = "Etiam tristique, risus ac pellentesque ullamcorper, mauris nisl tincidunt dui, sit amet porttitor eros nisl a dolor. " +
                    "Mauris eu sapien tincidunt, pulvinar leo a, tincidunt orci. In leo justo, hendrerit at convallis nec, semper in neque. Nunc " +
                    "at metus eros. Aliquam erat volutpat. Sed nec faucibus leo, quis cursus nisl.",
            images = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRhAHUz_3weYlC2aCZNSsna_PNEqGHZ1Di0Eg&s"),
            location = Location(43230.1, 753948.7, "Colombia", "Armenia"),
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

    val report = storedReports.find { it.id == reportId } ?: return

    var switchChecked by rememberSaveable { mutableStateOf(report.isResolved) }
    var title by rememberSaveable { mutableStateOf(report.title) }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf(report.category.displayName) }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = Category.entries.map { it.displayName }
    val dropDownError = dropDownValue.isBlank()

    var description by rememberSaveable { mutableStateOf(report.description) }
    var descriptionTouched by rememberSaveable { mutableStateOf(false) }
    val descriptionError = descriptionTouched && (description.isBlank() || description.length < 10)

    val locationError = false

    var photos by rememberSaveable { mutableStateOf(report.images) }
    var photoTouchedList by rememberSaveable { mutableStateOf(List(photos.size) { false }) }
    var photoErrors by remember { mutableStateOf(List(photos.size) { false }) }

    LaunchedEffect(photos, photoTouchedList) {
        isValidating = true
        delay(100)
        photoErrors = photos.mapIndexed { i, url ->
            val touched = photoTouchedList.getOrElse(i) { false }
            touched && !isImageValid(context, url)
        }
        isValidating = false
    }

    LaunchedEffect(photos.size) {
        if (photoTouchedList.size != photos.size) {
            photoTouchedList = List(photos.size) { i -> photoTouchedList.getOrElse(i) { false } }
        }
        if (photoErrors.size != photos.size) {
            photoErrors = List(photos.size) { i -> photoErrors.getOrElse(i) { false } }
        }
    }

    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var locationVisible by rememberSaveable { mutableStateOf("") }
    var locationNotVisible: Location? = null

    LaunchedEffect(Unit) {
        if(latitude != null && longitude != null){
            val locationName = getLocationName(context, latitude, longitude)
            city = locationName.first ?: ""
            country = locationName.second ?: ""
            locationNotVisible = Location(latitude = latitude, longitude = longitude, country = country, city = city)
            locationVisible = locationNotVisible.toString()
        }
    }

    val onAddPhoto = {
        photos = photos + ""
        photoTouchedList = photoTouchedList + false
    }

    val onRemovePhoto: (Int) -> Unit = { index ->
        if (index in photos.indices) {
            photos = photos.toMutableList().also { it.removeAt(index) }
            photoTouchedList = photoTouchedList.toMutableList().also {
                if (index < it.size) it.removeAt(index)
            }
        }
    }

    val onValueChangePhotos: (List<String>) -> Unit = { updatedList ->
        val changedIndex = updatedList.indexOfFirstIndexed { i, url -> url != photos.getOrNull(i) }
        photos = updatedList
        photoTouchedList = photoTouchedList.toMutableList().also {
            if (changedIndex in updatedList.indices) {
                while (it.size < updatedList.size) it.add(false)
                it[changedIndex] = true
            }
        }
    }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var saveReportVisible by rememberSaveable { mutableStateOf(false) }

    if (titleTouched || dropDownTouched || descriptionTouched || photoTouchedList.any { it }) {
        BackHandler { exitDialogVisible = true }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.edit_report),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    if (titleTouched || dropDownTouched || descriptionTouched || photoTouchedList.any { it }) {
                        exitDialogVisible = true
                    } else navigateBack()
                },
                actions = false
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReportForm(
                    title = title,
                    onValueChangeTitle = {
                        title = it
                        titleTouched = true
                    },
                    titleError = titleError,
                    placeHolder = stringResource(id = R.string.category),
                    value = dropDownValue,
                    onValueChange = {
                        dropDownValue = it
                        dropDownTouched = true
                        dropDownExpanded = false
                    },
                    items = categories,
                    dropDownError = dropDownError,
                    isExpanded = dropDownExpanded,
                    onExpandedChange = {
                        dropDownExpanded = it
                        dropDownTouched = true
                    },
                    isTouched = dropDownTouched,
                    onDismissRequest = { dropDownExpanded = false },
                    description = description,
                    onValueChangeDescription = {
                        description = it
                        descriptionTouched = true
                    },
                    descriptionError = descriptionError,
                    location = if (latitude != null && longitude != null) locationVisible else report.location.toString(),
                    onValueChangeLocation = { },
                    locationError = locationError,
                    navigateToReportLocation = navigateToReportLocation,
                    onClickCreate = {
                        if (!isValidating) saveReportVisible = true
                    },
                    photos = photos,
                    photoErrors = photoErrors,
                    onValueChangePhotos = onValueChangePhotos,
                    onAddPhoto = onAddPhoto,
                    onRemovePhoto = onRemovePhoto,
                    editMode = true,
                    switchChecked = switchChecked,
                    switchCheckedOnClick = {
                        switchChecked = it
                    },
                    isLoading = isValidating,
                    latitude = latitude,
                    longitude = longitude,
                    isEditing = true
                )
            }
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.exit_report_editing),
            message = stringResource(id = R.string.exit_report_editing_description),
            onClose = { exitDialogVisible = false },
            onExit = {
                exitDialogVisible = false
                navigateBack()
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.exit)
        )
    }

    if (saveReportVisible) {
        GenericDialog(
            title = stringResource(id = R.string.edit_report_title),
            message = stringResource(id = R.string.edit_report_description),
            onClose = { saveReportVisible = false },
            onExit = {
                saveReportVisible = false
                report.title = title
                report.category = Category.entries.first { it.displayName == dropDownValue }
                report.description = description
                report.isResolved = switchChecked
                report.location = locationNotVisible
                report.images = photos
                if ((titleTouched || dropDownTouched || descriptionTouched || photoTouchedList.any { it }) && report.rejectionDate != null) {
                    report.rejectionDate = null
                }
                navigateBack() //TODO: add proper navigation when saving the report
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.edit)
        )
    }
}

private inline fun <T> List<T>.indexOfFirstIndexed(predicate: (index: Int, T) -> Boolean): Int {
    for (i in indices) {
        if (predicate(i, this[i])) return i
    }
    return -1
}