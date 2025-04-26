package com.mapify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.theme.Spacing

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
    photoErrors: List<Boolean>,
    onValueChangePhotos: (List<String>) -> Unit,
    switchChecked: Boolean = false,
    switchCheckedOnClick: ((Boolean) -> Unit)? = null,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
    isLoading: Boolean
) {
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
            leadingIcon = { Icon(Icons.Outlined.Title, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(Modifier.height(Spacing.Inline))
        GenericDropDownMenu(
            placeholder = placeHolder,
            value = value,
            onValueChange = onValueChange,
            items = items,
            isError = dropDownError,
            supportingText = stringResource(R.string.category_suporting_test),
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched,
            leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        )
        GenericTextField(
            modifier = Modifier.height(160.dp),
            value = description,
            label = stringResource(R.string.description),
            onValueChange = onValueChangeDescription,
            isError = descriptionError,
            supportingText = stringResource(R.string.description_supporting_text),
            isSingleLine = false,
            leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        )
        GenericTextField(
            value = location,
            supportingText = stringResource(R.string.location_supporting_text),
            label = stringResource(R.string.location),
            onValueChange = onValueChangeLocation,
            isError = locationError,
            leadingIcon = {
                IconButton(onClick = navigateToReportLocation) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            },
            readOnly = true
        )
        photos.forEachIndexed { index, photo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenericTextField(
                    modifier = Modifier.weight(1f),
                    value = photo,
                    supportingText = stringResource(R.string.photo_supporting_text),
                    label = stringResource(R.string.photo) + " ${index + 1}",
                    onValueChange = {
                        val updatedPhotos = photos.toMutableList()
                        updatedPhotos[index] = it
                        onValueChangePhotos(updatedPhotos)
                    },
                    isError = photoErrors[index],
                    leadingIcon = { Icon(Icons.Outlined.ImageSearch, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    showTrailingIcon = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (index == 0) onAddPhoto() else onRemovePhoto(index)
                            },
                            enabled = if (index == 0) photos.size < 5 else true
                        ) {
                            Icon(
                                imageVector = if (index == 0) Icons.Outlined.Add else Icons.Outlined.Remove,
                                contentDescription = null
                            )
                        }
                    }
                )
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
        val arePhotosValid = photos.isEmpty() || (photos.none { it.isBlank() } && photoErrors.all { !it })
        val isButtonEnabled = !titleError && title.isNotBlank() && !dropDownError && !descriptionError && description.isNotBlank() && arePhotosValid && !isLoading
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