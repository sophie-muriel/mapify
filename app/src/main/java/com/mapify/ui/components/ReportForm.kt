package com.mapify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    photo: String,
    onValueChangePhoto: (String) -> Unit,
    navigateToReportLocation: () -> Unit,
    photoError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GenericTextField(
            value = title,
            supportingText = "You must provide a title",
            label = "Title",
            onValueChange = onValueChangeTitle,
            isError = titleError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(Spacing.Inline))

        GenericDropDownMenu(
            placeholder = placeHolder,
            value = value,
            onValueChange = onValueChange,
            items = items,
            isError = dropDownError,
            supportingText = "Select a City",
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched
        )

        GenericTextField(
            modifier = Modifier.aspectRatio(2f),
            value = description,
            label = "Description",
            onValueChange = onValueChangeDescription,
            isError = descriptionError,
            supportingText = "Add a description",
            isSingleLine = false,
        )

        GenericTextField(
            value = location,
            supportingText = "A location must be provided",
            label = "Location",
            onValueChange = onValueChangeLocation,
            isError = locationError,
            leadingIcon = {
                IconButton(
                    onClick = {
                        navigateToReportLocation()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            readOnly = true
        )

        GenericTextField(
            value = photo,
            supportingText = "A photo url must be provided",
            label = "Photo",
            onValueChange = onValueChangePhoto,
            isError = photoError,
            leadingIcon = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ImageSearch,
                        contentDescription = "Location Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}