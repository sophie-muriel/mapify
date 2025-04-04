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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    photo: String,
    onValueChangePhoto: (String) -> Unit,
    navigateToReportLocation: () -> Unit,
    photoError: Boolean,
    onClickCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GenericTextField(
            value = title,
            supportingText = stringResource( id = R.string.title_supporting_text ),
            label = stringResource( id = R.string.title ),
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
            supportingText = stringResource( id = R.string.category_suporting_test ),
            isExpanded = isExpanded,
            onExpandedChange = onExpandedChange,
            onDismissRequest = onDismissRequest,
            isTouched = isTouched
        )

        GenericTextField(
            modifier = Modifier.height(160.dp),
            value = description,
            label = stringResource( id = R.string.description ),
            onValueChange = onValueChangeDescription,
            isError = descriptionError,
            supportingText = stringResource( id = R.string.description_supporting_text ),
            isSingleLine = false,
        )

        GenericTextField(
            value = location,
            supportingText = stringResource( id = R.string.location_supporting_text ),
            label = stringResource( id = R.string.location ),
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
                        contentDescription = stringResource( id = R.string.location_icon ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            readOnly = true
        )

        GenericTextField(
            value = photo,
            supportingText = stringResource( id = R.string.photo_supporting_text ),
            label = stringResource( id = R.string.photo ),
            onValueChange = onValueChangePhoto,
            isError = photoError,
            leadingIcon = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ImageSearch,
                        contentDescription = stringResource( id = R.string.photo_icon ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = title.isNotBlank() && value.isNotBlank() && description.isNotBlank() && photo.isNotBlank(), //Location has to be added here later
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(
                text = stringResource(id = R.string.create),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}