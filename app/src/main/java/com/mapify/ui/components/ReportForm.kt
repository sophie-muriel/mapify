package com.mapify.ui.components

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    switchCheckedValue: Boolean = false,
    switchCheckedOnClick: ((Boolean) -> Unit)? = null,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
) {

    val regex = Regex("^(https?:\\/\\/)?([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})(\\/\\S*)?$")

    val switchChecked = remember { mutableStateOf(switchCheckedValue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.Sides), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GenericTextField(
            value = title,
            supportingText = stringResource(id = R.string.title_supporting_text),
            label = stringResource(id = R.string.title),
            onValueChange = onValueChangeTitle,
            isError = titleError,
            leadingIcon = {
                IconButton(
                    onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Title,
                        contentDescription = stringResource(id = R.string.title_icon_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(Spacing.Inline))

        GenericDropDownMenu(
            placeholder = placeHolder,
            value = value,
            onValueChange = onValueChange,
            items = items,
            isError = dropDownError,
            supportingText = stringResource(id = R.string.category_suporting_test),
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched,
            leadingIcon = {
                IconButton(
                    onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = stringResource(id = R.string.category_icon_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        GenericTextField(
            modifier = Modifier.height(160.dp),
            value = description,
            label = stringResource(id = R.string.description),
            onValueChange = onValueChangeDescription,
            isError = descriptionError,
            supportingText = stringResource(id = R.string.description_supporting_text),
            isSingleLine = false,
            leadingIcon = {
                Icon(
                    modifier = Modifier.align(Alignment.Start),
                    imageVector = Icons.Outlined.Description,
                    contentDescription = stringResource(id = R.string.description_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )

            })

        GenericTextField(
            value = location,
            supportingText = stringResource(id = R.string.location_supporting_text),
            label = stringResource(id = R.string.location),
            onValueChange = onValueChangeLocation,
            isError = locationError,
            leadingIcon = {
                IconButton(
                    onClick = { navigateToReportLocation() }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = stringResource(id = R.string.location_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            readOnly = true
        )

        photos.forEachIndexed { i, image ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenericTextField(
                    modifier = Modifier.weight(1f),
                    value = photos[i],
                    supportingText = stringResource(id = R.string.photo_supporting_text),
                    label = stringResource(id = R.string.photo) + " ${i + 1}",
                    onValueChange = {
                        val updatedPhotos = photos.toMutableList()
                        updatedPhotos[i] = it
                        onValueChangePhotos(updatedPhotos)
                    },
                    isError = photoErrors[i],
                    leadingIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Outlined.ImageSearch,
                                contentDescription = stringResource(id = R.string.photo_icon),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    showTrailingIcon = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (i == 0) {
                                    onAddPhoto()
                                } else {
                                    onRemovePhoto(i)
                                }
                            },
                            enabled = if (i == 0) photos.size < 5 else true
                        ) {
                            Icon(
                                imageVector = if (i == 0) Icons.Outlined.Add else Icons.Outlined.Remove,
                                contentDescription = if (i == 0)
                                    stringResource(id = R.string.add_icon_description)
                                else
                                    stringResource(id = R.string.remove_icon_description)
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
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.is_report_solved),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        )
                    )
                }

                Switch(
                    checked = switchChecked.value,
                    onCheckedChange = switchCheckedOnClick,
                    modifier = Modifier
                        .scale(0.85f)
                )
            }
        }

        Spacer(Modifier.height(Spacing.Large))

        val vogosBinted = photos.isEmpty() || photoErrors.all { !it } // vorp?
        val noBlanks = if (photos.isEmpty()) true else photos.none { it.isBlank() }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = !titleError && title.isNotBlank() && !dropDownError && !descriptionError
                    && description.isNotBlank() && vogosBinted && noBlanks, //TODO: Location has to be added here later
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(
                text = if (editMode) stringResource(id = R.string.edit) else stringResource(id = R.string.create),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(Spacing.Large))
    }
}