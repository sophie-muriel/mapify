package com.mapify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.components.ReportForm
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.SimpleTopBar
import com.mapify.utils.isImageValid
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun EditReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: () -> Unit,
) {
    //TODO: receive report id
    val context = LocalContext.current
    var isValidating by remember { mutableStateOf(false) }

    val report = Report(
        id = "1",
        title = "Report 1",
        category = Category.SECURITY,
        description = "This is a report",
        images = listOf(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRkmoJWVhxab15KM_FQbk539hzwjN7qhyWeDw&s",
            "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQd1kWKsODGmz1P44kiLTfpeIOkaemYITnaRVOZEn372xCyrpNoQQ_dMDAV4dWLpVTDFekNEtlkJaDnhlTzoQWdNg",
            "https://images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg?cs=srgb&dl=pexels-pixabay-104827.jpg&fm=jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyUELlFEGUb5UvoFSrH4kM6W5g9ALrxcbxQ5OJ2lH4rUo6qEQya0siFpNSeMx6pku24eQ&usqp=CAU"
        ),
        location = Location(
            latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
        ),
        status = ReportStatus.NOT_VERIFIED,
        userId = "1",
        date = LocalDateTime.now()
    )

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
        val validated = photos.mapIndexed { i, url ->
            val touched = photoTouchedList.getOrElse(i) { false }
            if (touched) !isImageValid(context, url) else false
        }
        photoErrors = validated
        isValidating = false
    }

    LaunchedEffect(photos.size) {
        if (photoTouchedList.size != photos.size) {
            photoTouchedList = List(photos.size) { i ->
                photoTouchedList.getOrElse(i) { false }
            }
        }
        if (photoErrors.size != photos.size) {
            photoErrors = List(photos.size) { i ->
                photoErrors.getOrElse(i) { false }
            }
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
                while (it.size < updatedList.size) {
                    it.add(false)
                }
                it[changedIndex] = true
            }
        }
    }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var saveReportVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {
        exitDialogVisible = true
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.CenterStart,
                stringResource(id = R.string.edit_report),
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    exitDialogVisible = true
                },
                false
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
                    onDismissRequest = {
                        dropDownExpanded = false
                    },
                    description = description,
                    onValueChangeDescription = {
                        description = it
                        descriptionTouched = true
                    },
                    descriptionError = descriptionError,
                    location = report.location.toString(),
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
                    isLoading = isValidating
                )
            }
        }
    }
    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.exit_report_editing),
            message = stringResource(id = R.string.exit_report_editing_description),
            onClose = {
                exitDialogVisible = false
            },
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
            onClose = {
                saveReportVisible = false
            },
            onExit = {
                saveReportVisible = false
                report.title = title
                report.category = Category.entries.first { it.displayName == dropDownValue }
                report.description = description
                report.isResolved = switchChecked
                report.images = photos
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