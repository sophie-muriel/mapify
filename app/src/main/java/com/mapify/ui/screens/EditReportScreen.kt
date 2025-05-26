package com.mapify.ui.screens

import android.os.Build
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
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.saveable.rememberSaveable
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResult
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
    var isValidating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var navigateAfterUpdate by remember { mutableStateOf(false) }

    val reportsViewModel = LocalMainViewModel.current.reportsViewModel
    val reportRequestResult by reportsViewModel.reportRequestResult.collectAsState()

    LaunchedEffect(reportId) {
        reportsViewModel.findById(reportId)
    }

    val report by reportsViewModel.currentReport.collectAsState()

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

    var photos by rememberSaveable { mutableStateOf(report!!.images.toMutableList()) }
    var photoTouchedList by rememberSaveable { mutableStateOf(List(photos.size) { false }) }
    var photoErrors by remember { mutableStateOf(List(photos.size) { false }) }
    var validatingImageIndex by remember { mutableStateOf<Int?>(null) }
    var changedPhotoIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(photos, photoTouchedList) {
        isValidating = true
        val index = changedPhotoIndex
        if (index != null && index in photos.indices) {
            validatingImageIndex = index
            delay(100)

            val isInvalid = !isImageValid(context, photos[index])
            photoErrors = photoErrors.toMutableList().also {
                if (index < it.size) it[index] = isInvalid
            }

            validatingImageIndex = null
            changedPhotoIndex = null
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

    val onAddPhoto = {
        photos = photos.toMutableList().apply { add("") }
        photoTouchedList = photoTouchedList.toMutableList().apply { add(false) }
        photoErrors = photoErrors.toMutableList().apply { add(false) }
    }

    val onRemovePhoto: (Int) -> Unit = { index ->
        if (index in photos.indices) {
            photos = photos.toMutableList().also { it.removeAt(index) }
            photoTouchedList = photoTouchedList.toMutableList().also { it.removeAt(index) }
            photoErrors = photoErrors.toMutableList().also { it.removeAt(index) }
            if (changedPhotoIndex == index || (changedPhotoIndex ?: -1) >= photos.size) {
                changedPhotoIndex = null
            }
            if (validatingImageIndex == index || (validatingImageIndex ?: -1) >= photos.size) {
                validatingImageIndex = null
            }
        }
    }

    val onValueChangePhotos: (List<String>) -> Unit = { updatedList ->
        val changedIndex = updatedList.indexOfFirstIndexed { i, url -> url != photos.getOrNull(i) }
        photos = updatedList.toMutableList()
        photoTouchedList = photoTouchedList.toMutableList().also {
            if (changedIndex in updatedList.indices) {
                while (it.size < updatedList.size) it.add(false)
                it[changedIndex] = true
            }
        }
        changedPhotoIndex = changedIndex
    }

    var locationVisible by rememberSaveable { mutableStateOf("") }
    var locationNotVisible by remember { mutableStateOf<Location?>(null) }

    LaunchedEffect(Unit) {
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
        delay(2000)
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
                    validatingImageIndex = validatingImageIndex,
                    isLoading = !isValidating && isLoading,
                    latitude = latitude,
                    longitude = longitude,
                    isEditing = true
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
                            delay(1000)
                            reportsViewModel.resetReportRequestResult()
                            if(navigateAfterUpdate){
                                navigateBack()
                            }
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
                            delay(1000)
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
                        if ((titleTouched || dropDownTouched || descriptionTouched || photoTouchedList.any { it }) && report!!.rejectionDate != null) {
                            updatedReport.rejectionDate = null
                        }
                        reportsViewModel.update(updatedReport)
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

private inline fun <T> List<T>.indexOfFirstIndexed(predicate: (index: Int, T) -> Boolean): Int {
    for (i in indices) {
        if (predicate(i, this[i])) return i
    }
    return -1
}