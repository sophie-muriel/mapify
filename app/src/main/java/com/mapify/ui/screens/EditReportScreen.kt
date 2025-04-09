package com.mapify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.components.ReportForm
import com.mapify.model.Report
import com.mapify.model.ReportStatus
import com.mapify.model.Category
import com.mapify.model.Location
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.SimpleTopBar
import java.time.LocalDateTime

@Composable
fun EditReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: () -> Unit,
) {
    val report = Report(
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
    )

    val switchChecked = remember { mutableStateOf(false) }

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

    val regex = Regex("^(https?:\\/\\/)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(\\/\\S*)?$")
    var photos by rememberSaveable { mutableStateOf(report.images) }
    var photoTouchedList by rememberSaveable { mutableStateOf(listOf(false)) }
    val photoErrors = photos.mapIndexed { i, url ->
        val touched = photoTouchedList.getOrElse(i) { false }
        touched && !regex.matches(url)
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

    val isKeyboardActive = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var saveReportVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {
        exitDialogVisible = true
    }

    Scaffold(
        topBar = {
            if (!isKeyboardActive) {
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
            }
        }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    saveReportVisible = true
                },
                photos = photos,
                photoErrors = photoErrors,
                onValueChangePhotos = onValueChangePhotos,
                onAddPhoto = onAddPhoto,
                onRemovePhoto = onRemovePhoto,
                editMode = true,
                switchCheckedValue = switchChecked.value,
                switchCheckedOnClick = {
                    switchChecked.value = it
                }
            )
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
                report.category = enumValueOf(dropDownValue)
                report.description = description
                report.isResolved = switchChecked.value
                // report.images =
                //TODO: figure out how to save a list of images
                navigateBack()
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