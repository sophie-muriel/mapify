package com.mapify.ui.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.delay
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResultEffectHandler
import updateCityCountry

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EditReportScreen(
    navigateBack: () -> Unit,
    navigateToReportLocation: (Double?, Double?) -> Unit,
    reportId: String,
    latitude: Double? = null,
    longitude: Double? = null
) {

    val context = LocalContext.current

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()
    var isLoading = rememberSaveable { mutableStateOf(false) }

    var navigateAfterUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(reportId) {
        reportsViewModel.findById(reportId)
    }

    val report by reportsViewModel.currentReport.collectAsState()

    if (report == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
                strokeWidth = 4.dp
            )
        }
        return
    }

    var switchChecked by rememberSaveable { mutableStateOf(report!!.isResolved) }
    var title by rememberSaveable { mutableStateOf(report!!.title) }
    var titleTouched by rememberSaveable { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()

    var dropDownValue by rememberSaveable { mutableStateOf(report!!.category.displayName) }
    var dropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var dropDownTouched by rememberSaveable { mutableStateOf(false) }
    val categories = Category.entries.map { it.displayName }
    val dropDownError = dropDownValue.isBlank()

    var description by rememberSaveable { mutableStateOf(report!!.description) }
    var descriptionTouched by rememberSaveable { mutableStateOf(false) }
    val descriptionError = descriptionTouched && (description.isBlank() || description.length < 10)

    val locationError = false

    var photos by remember { mutableStateOf(report!!.images) }

    val onAddPhoto: (String) -> Unit = { newPhotoUrl ->
        photos = photos + newPhotoUrl
    }

    val onRemovePhoto: (Int) -> Unit = { index ->
        if (index in photos.indices) {
            photos = photos.toMutableList().also { it.removeAt(index) }
        }
    }

    var locationVisible by rememberSaveable { mutableStateOf("") }
    var locationNotVisible by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(latitude, longitude, report) {
        if (latitude != null && longitude != null) {
            val loc = Location(latitude, longitude)
            loc.updateCityCountry(context)
            locationNotVisible = loc
            locationVisible = loc.toString()
        }else{
            val loc = report!!.location
            loc?.updateCityCountry(context)
            locationNotVisible = loc
            locationVisible = loc?.toString().orEmpty()
        }
        delay(1000)
    }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }
    var saveReportVisible by rememberSaveable { mutableStateOf(false) }

    val hasChanges = remember(title, dropDownValue, description, photos, switchChecked, latitude, longitude, report) {
        title != report!!.title || dropDownValue != report!!.category.displayName ||
                description != report!!.description || photos != report!!.images ||
                switchChecked != report!!.isResolved ||
                (latitude != null && longitude != null && (latitude != report!!.location?.latitude || longitude != report!!.location?.longitude))
    }

    BackHandler(enabled = hasChanges) {
        exitDialogVisible = true
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.edit_report),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    if (hasChanges) {
                        exitDialogVisible = true
                    } else {
                        navigateBack()
                    }
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
                    location = locationVisible,
                    onValueChangeLocation = { },
                    locationError = locationError,
                    navigateToReportLocation = {
                        if(latitude != null && longitude != null){
                            navigateToReportLocation(latitude, longitude)
                        }else{
                            navigateToReportLocation(report!!.location?.latitude, report!!.location?.longitude)
                        }
                    },
                    onClickCreate = {
                        if (photos.isNotEmpty()) saveReportVisible = true
                    },
                    hasChanged = hasChanges,
                    photos = photos,
                    onAddPhoto = onAddPhoto,
                    onRemovePhoto = onRemovePhoto,
                    editMode = true,
                    switchChecked = switchChecked,
                    switchCheckedOnClick = {
                        switchChecked = it
                    },
                    isLoading = isLoading.value,
                    latitude = latitude,
                    longitude = longitude,
                    isEditing = true,
                    context = context
                )
                RequestResultEffectHandler(
                    requestResult = reportRequestResult,
                    context = context,
                    isLoading = isLoading,
                    onResetResult = { reportsViewModel.resetReportRequestResult() },
                    onNavigate = {
                        if(navigateAfterUpdate){
                            navigateBack()
                        }
                    }
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
                try {
                    val updatedReport = createUpdatedReport(report)
                    if (updatedReport != null){
                        updatedReport.title = title
                        updatedReport.category = Category.entries.first { it.displayName == dropDownValue }
                        updatedReport.description = description
                        updatedReport.images = photos.toList()
                        updatedReport.location = locationNotVisible
                        updatedReport.isResolved = switchChecked
                        if (hasChanges && updatedReport.rejectionDate != null) {
                            updatedReport.rejectionDate = null
                        }
                        reportsViewModel.update(updatedReport, 1)
                        navigateAfterUpdate = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Report saving error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.edit)
        )
    }
}