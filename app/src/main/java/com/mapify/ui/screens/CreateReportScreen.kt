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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.mapify.model.Role
import com.mapify.model.User
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.SimpleTopBar
import java.time.LocalDateTime

@Composable
fun CreateReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: () -> Unit,
    navigateToReportView: (String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf("") }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = Category.entries.map { it.displayName }
    val dropDownError = dropDownValue.isBlank()

    var description by rememberSaveable { mutableStateOf("") }
    var descriptionTouched by rememberSaveable { mutableStateOf(false) }
    val descriptionError = descriptionTouched && (description.isBlank() || description.length < 10)

    var location by rememberSaveable { mutableStateOf("") }
    val locationError = false

    val regex = Regex("^(https?:\\/\\/)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(\\/\\S*)?$")
    var photos by rememberSaveable { mutableStateOf(listOf("")) }
    var photoTouchedList by rememberSaveable { mutableStateOf(listOf(false)) }
    val photoErrors = photos.mapIndexed { i, url ->
        val touched = photoTouchedList.getOrElse(i) { false }
        touched && !regex.matches(url)
    }

    val onAddPhoto = {
        photos = photos + "" // Adds a new empty field
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
        // Find the first changed photo (if any)
        val changedIndex = updatedList.indexOfFirstIndexed { i, url -> url != photos.getOrNull(i) }

        // Update photos
        photos = updatedList

        // Update only touched index
        photoTouchedList = photoTouchedList.toMutableList().also {
            if (changedIndex in updatedList.indices) {
                // Expand touched list if needed
                while (it.size < updatedList.size) {
                    it.add(false)
                }
                it[changedIndex] = true
            }
        }
    }

    val reportsList = remember { mutableStateListOf<Report>() }
    val embeddedUser = User(
        id = "1",
        fullName = "Embedded User",
        email = "embedded@mail.com",
        password = "ThisIsATestPass",
        role = Role.CLIENT,
        registrationLocation = Location(
            latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
        )
    )
    var reportsIdCounter by rememberSaveable { mutableIntStateOf(4) }

    val isKeyboardActive = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var publishReportVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = true) {
        exitDialogVisible = true
    }

    Scaffold(
        topBar = {
            if (!isKeyboardActive) {
                SimpleTopBar(
                    Alignment.CenterStart,
                    stringResource(id = R.string.create_report),
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
                location = location,
                onValueChangeLocation = {
                    location = it
                },
                locationError = locationError,
                navigateToReportLocation = navigateToReportLocation,
                onClickCreate = {
                    publishReportVisible = true
                },
                editMode = false,
                photos = photos,
                photoErrors = photoErrors,
                onValueChangePhotos = onValueChangePhotos,
                onAddPhoto = onAddPhoto,
                onRemovePhoto = onRemovePhoto
            )
        }

    }
    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.exit_report_creation),
            message = stringResource(id = R.string.exit_report_creation_description),
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

    if (publishReportVisible) {
        GenericDialog(
            title = stringResource(id = R.string.publish_report),
            message = stringResource(id = R.string.publish_report_description),
            onClose = {
                publishReportVisible = false
            },
            onExit = {
                publishReportVisible = false
                val newReport = Report(
                    title = title,
                    category = Category.entries.find { it.displayName == dropDownValue }!!,
                    description = description,
                    location = null, //TODO: This must be changed here and in Report model erase the "?"
                    images = photos,
                    id = reportsIdCounter.toString(),
                    status = ReportStatus.NOT_VERIFIED,
                    userId = embeddedUser.id,
                    date = LocalDateTime.now()
                )
                reportsIdCounter++
                reportsList.add(newReport)
                navigateToReportView(newReport.id)
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.publish)
        )
    }
}

private inline fun <T> List<T>.indexOfFirstIndexed(predicate: (index: Int, T) -> Boolean): Int {
    for (i in indices) {
        if (predicate(i, this[i])) return i
    }
    return -1
}