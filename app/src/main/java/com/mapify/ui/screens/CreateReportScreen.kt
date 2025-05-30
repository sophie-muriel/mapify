package com.mapify.ui.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.saveable.rememberSaveable
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResultEffectHandler
import com.mapify.utils.SharedPreferencesUtils
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

    var photos by remember { mutableStateOf(emptyList<String>()) }

    var location by rememberSaveable { mutableStateOf("") }
    val locationError = false

    val onAddPhoto: (String) -> Unit = { newPhotoUrl ->
        photos = photos + newPhotoUrl
    }

    val onRemovePhoto: (Int) -> Unit = { index ->
        if (index in photos.indices) {
            photos = photos.toMutableList().also { it.removeAt(index) }
        }
    }

    var isLoading = rememberSaveable { mutableStateOf(false) }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var publishReportVisible by rememberSaveable { mutableStateOf(false) }

    BackHandler { exitDialogVisible = true }
    var locationVisible by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (latitude != null && longitude != null) {
            val loc = Location(latitude, longitude)
            loc.updateCityCountry(context)
            locationVisible = loc.toString()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            reportsViewModel.resetCreatedReportId()
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
                    onClickCreate = { if (photos.isNotEmpty()) publishReportVisible = true },
                    editMode = false,
                    photos = photos,
                    onAddPhoto = onAddPhoto,
                    onRemovePhoto = onRemovePhoto,
                    isLoading = isLoading.value,
                    latitude = latitude,
                    longitude = longitude,
                    context = context
                )
                RequestResultEffectHandler(
                    requestResult = reportRequestResult,
                    context = context,
                    isLoading = isLoading,
                    onResetResult = { reportsViewModel.resetReportRequestResult() },
                    onNavigate = {
                        if (navigateAfterCreate) {
                            createdReportId?.let { navigateToReportView(it) }
                        }
                    }
                )
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
                    userId = userId ?: ""
                )
                reportsViewModel.create(newReport)
                navigateAfterCreate = true
            },
            onCloseText = stringResource(R.string.cancel),
            onExitText = stringResource(R.string.publish)
        )
    }
}