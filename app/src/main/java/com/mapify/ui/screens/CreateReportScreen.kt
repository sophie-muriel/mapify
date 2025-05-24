package com.mapify.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import androidx.compose.runtime.saveable.rememberSaveable
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResult
import com.mapify.utils.SharedPreferencesUtils
import okhttp3.internal.wait
import updateCityCountry

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreateReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: () -> Unit,
    navigateToReportView: (String) -> Unit,
    latitude: Double? = null,
    longitude: Double? = null
) {

    val context = LocalContext.current

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()
    var navigateAfterCreate by remember { mutableStateOf(false) }
    val createdReportId by reportsViewModel.createdReportId.collectAsState()

    var isValidating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf("") }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf("") }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = Category.entries.map { it.displayName }
    val dropDownError = dropDownTouched && dropDownValue.isBlank()

    var description by rememberSaveable { mutableStateOf("") }
    var descriptionTouched by rememberSaveable { mutableStateOf(false) }
    val descriptionError = descriptionTouched && (description.isBlank() || description.length < 10)

    var location by rememberSaveable { mutableStateOf("") }
    val locationError = false

    var photos by rememberSaveable { mutableStateOf(listOf("")) }
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
        photoTouchedList = List(photos.size) { i -> photoTouchedList.getOrElse(i) { false } }
        photoErrors = List(photos.size) { i -> photoErrors.getOrElse(i) { false } }
    }

    val onAddPhoto = {
        photos = photos + ""
        photoTouchedList = photoTouchedList + false
        photoErrors = photoErrors + false
    }

    val onRemovePhoto: (Int) -> Unit = { index ->
        if (index in photos.indices) {
            photos = photos.toMutableList().also { it.removeAt(index) }
            photoTouchedList = photoTouchedList.toMutableList().also { if (index < it.size) it.removeAt(index) }
            photoErrors = photoErrors.toMutableList().also { if (index < it.size) it.removeAt(index) }
        }
    }

    val onValueChangePhotos: (List<String>) -> Unit = { updatedList ->
        val changedIndex = updatedList.indexOfFirstIndexed { i, url -> url != photos.getOrNull(i) }
        photos = updatedList
        photoTouchedList = photoTouchedList.toMutableList().apply {
            while (size < updatedList.size) add(false)
            if (changedIndex in updatedList.indices) this[changedIndex] = true
        }
        photoErrors = photoErrors.toMutableList().apply {
            while (size < updatedList.size) add(false)
        }
    }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var publishReportVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler { exitDialogVisible = true }
    var locationVisible by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = true) {
        exitDialogVisible = true
    }

    LaunchedEffect(Unit) {
        if (latitude != null && longitude != null) {
            val loc = Location(latitude, longitude)
            loc.updateCityCountry(context)
            locationVisible = loc.toString()
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(R.string.create_report),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(R.string.back_arrow_icon),
                onClickNavIcon = { exitDialogVisible = true },
                actions = false
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReportForm(
                    title = title,
                    onValueChangeTitle = {
                        title = it
                        titleTouched = true
                    },
                    titleError = titleError,
                    placeHolder = stringResource(R.string.category),
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
                    onDismissRequest = { dropDownExpanded = false },
                    isTouched = dropDownTouched,
                    description = description,
                    onValueChangeDescription = {
                        description = it
                        descriptionTouched = true
                    },
                    descriptionError = descriptionError,
                    location = if (latitude != null && longitude != null) locationVisible else location,
                    onValueChangeLocation = {
                        location = it
                    },
                    locationError = locationError,
                    navigateToReportLocation = navigateToReportLocation,
                    onClickCreate = { if (!isValidating) publishReportVisible = true },
                    editMode = false,
                    photos = photos,
                    photoErrors = photoErrors,
                    onValueChangePhotos = onValueChangePhotos,
                    onAddPhoto = onAddPhoto,
                    onRemovePhoto = onRemovePhoto,
                    isLoading = !isValidating && isLoading,
                    latitude = latitude,
                    longitude = longitude
                )

                when (reportRequestResult) {
                    null -> {
                        isLoading = false
                    }

                    is RequestResult.Success -> {
                        isLoading = false
                        LaunchedEffect(reportRequestResult) {
                            Toast.makeText(
                                context,
                                (reportRequestResult as RequestResult.Success).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(1500)
                            reportsViewModel.resetReportRequestResult()
                            reportsViewModel.resetCreatedReportId()
                            createdReportId?.let { navigateToReportView(it) }
                        }
                    }

                    is RequestResult.Failure -> {
                        isLoading = false
                        LaunchedEffect(reportRequestResult) {
                            Toast.makeText(
                                context,
                                (reportRequestResult as RequestResult.Failure).message,
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(2000)
                            reportsViewModel.resetReportRequestResult()
                        }
                    }

                    is RequestResult.Loading -> {
                        isLoading = true
                    }
                }
            }
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(R.string.exit_report_creation),
            message = stringResource(R.string.exit_report_creation_description),
            onClose = { exitDialogVisible = false },
            onExit = {
                exitDialogVisible = false
                navigateBack()
            },
            onCloseText = stringResource(R.string.cancel),
            onExitText = stringResource(R.string.exit)
        )
    }

    if (publishReportVisible) {
        GenericDialog(
            title = stringResource(R.string.publish_report),
            message = stringResource(R.string.publish_report_description),
            onClose = { publishReportVisible = false },
            onExit = {
                publishReportVisible = false
                val newReport = Report(
                    title = title,
                    category = Category.entries.find { it.displayName == dropDownValue }!!,
                    description = description,
                    location = Location.stringToLocation(locationVisible),
                    images = photos,
                    userId = userId?: ""
                )
                reportsViewModel.create(newReport)
                navigateAfterCreate = true
            },
            onCloseText = stringResource(R.string.cancel),
            onExitText = stringResource(R.string.publish)
        )
    }
}

private inline fun <T> List<T>.indexOfFirstIndexed(predicate: (index: Int, T) -> Boolean): Int {
    for (i in indices) {
        if (predicate(i, this[i])) return i
    }
    return -1
}