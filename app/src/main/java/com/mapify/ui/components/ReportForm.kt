package com.mapify.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.mapify.R
import com.mapify.ui.theme.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ReportForm(
    title: String,
    onValueChangeTitle: (String) -> Unit,
    titleError: Boolean,
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    dropDownError: Boolean,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isTouched: Boolean,
    onDismissRequest: () -> Unit,
    description: String,
    onValueChangeDescription: (String) -> Unit,
    descriptionError: Boolean,
    location: String,
    onValueChangeLocation: (String) -> Unit,
    locationError: Boolean,
    navigateToReportLocation: () -> Unit,
    onClickCreate: () -> Unit,
    editMode: Boolean = false,
    photos: List<String>,
    switchChecked: Boolean = false,
    switchCheckedOnClick: ((Boolean) -> Unit)? = null,
    onAddPhoto: (String) -> Unit,
    onRemovePhoto: (Int) -> Unit,
    isLoading: Boolean,
    latitude: Double? = null,
    longitude: Double? = null,
    isEditing: Boolean = false,
    hasChanged: Boolean = false,
    context: Context,
) {
    var internalIsLoading = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.Sides)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenericTextField(
            value = title,
            supportingText = stringResource(R.string.title_supporting_text),
            label = stringResource(R.string.title),
            onValueChange = onValueChangeTitle,
            isError = titleError,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Title,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(Modifier.height(Spacing.Inline))
        GenericDropDownMenu(
            placeholder = placeHolder,
            value = value,
            onValueChange = onValueChange,
            items = items,
            isError = dropDownError,
            supportingText = stringResource(R.string.category_supporting_test),
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        GenericTextField(
            modifier = Modifier.height(160.dp),
            value = description,
            label = stringResource(R.string.description),
            onValueChange = onValueChangeDescription,
            isError = descriptionError,
            supportingText = stringResource(R.string.description_supporting_text),
            isSingleLine = false,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        GenericTextField(
            value = location,
            supportingText = stringResource(R.string.location_supporting_text),
            label = stringResource(R.string.location),
            onValueChange = onValueChangeLocation,
            isError = locationError,
            leadingIcon = {
                IconButton(onClick = navigateToReportLocation) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            readOnly = true
        )

        Spacer(Modifier.height(Spacing.Inline))

        ReportImages(
            context = context,
            photosCount = photos.size,
            onPhotoSelected = { newPhotoUrl ->
                onAddPhoto(newPhotoUrl)
            },
            isLoading = internalIsLoading
        )
        Spacer(Modifier.height(Spacing.Large))


        if (photos.isNotEmpty()) {

            var dynamicHeight = 0
            if (photos.size in 1..3) {
                dynamicHeight = 125
            } else if (photos.size in 4..5) {
                dynamicHeight = 250
            }

            Text(
                text = "Selected Photos - ${photos.size}/5",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.Small)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small),
                modifier = Modifier
                    .height(dynamicHeight.dp)
                    .fillMaxWidth()
            ) {
                itemsIndexed(photos) { index, photoUrl ->
                    ElevatedCard(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = stringResource(
                                    id = R.string.upload_image_description,
                                    index + 1
                                ),
                                modifier = Modifier.fillMaxSize()
                            )
                            val scope = rememberCoroutineScope()
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        internalIsLoading.value = true
                                        onRemovePhoto(index)
                                        internalIsLoading.value = false
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(32.dp)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Cancel,
                                    contentDescription = stringResource(R.string.remove_uploaded_image),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (editMode && switchCheckedOnClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = Spacing.Sides),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.is_report_solved),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = switchChecked,
                    onCheckedChange = switchCheckedOnClick,
                    modifier = Modifier.scale(0.85f)
                )
            }
        }

        Spacer(Modifier.height(Spacing.Large))

        var isButtonEnabled = false
        if (!internalIsLoading.value && !isLoading) {
            if (isEditing && hasChanged && photos.size > 0) {
                isButtonEnabled = true
            } else if (!isEditing) {
                isButtonEnabled =
                    !titleError && title.isNotBlank() && !dropDownError && !descriptionError && description.isNotBlank() && !isLoading && photos.size > 0 && (isEditing || latitude != null && longitude != null)
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = isButtonEnabled,
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (editMode) stringResource(R.string.edit) else stringResource(R.string.create),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(Spacing.Large))
    }
}

@Composable
fun ReportImages(
    context: Context,
    photosCount: Int,
    onPhotoSelected: (String) -> Unit,
    isLoading: MutableState<Boolean> = mutableStateOf(false),
) {
    val config = mapOf(
        "cloud_name" to "dz06v0ogd",
        "api_key" to "712516261436128",
        "api_secret" to "uIW_1ozXBxyj3TprmhDPuvIvk_I"
    )
    val scope = rememberCoroutineScope()
    val cloudinary = Cloudinary(config)

    val errorUploadingImage = stringResource(id = R.string.error_uploading_image)
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                isLoading.value = true
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    inputStream?.let { stream ->
                        val result = cloudinary.uploader().upload(stream, ObjectUtils.emptyMap())
                        val photoUrl = result["secure_url"].toString()
                        onPhotoSelected(photoUrl)
                        isLoading.value = false
                    }
                } catch (e: Exception) {
                    with(Dispatchers.Main) {
                        Toast.makeText(context, errorUploadingImage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val imageAccessPermissionGranted = stringResource(id = R.string.image_access_permission_granted)
    val imageAccessPermissionDenied = stringResource(id = R.string.image_access_permission_denied)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, imageAccessPermissionGranted, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, imageAccessPermissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            onClick = {
                val permissionCheckResult =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                    } else {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    fileLauncher.launch("image/*")
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            },
            enabled = photosCount < 5,
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = stringResource(id = R.string.select_images))
            }
        }
    }
}