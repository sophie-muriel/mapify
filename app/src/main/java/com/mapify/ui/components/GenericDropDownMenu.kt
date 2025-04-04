package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericDropDownMenu(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    items: List<String>,
    isError: Boolean,
    supportingText: String,
    isTouched: Boolean,
    onDismissRequest: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {

    //var isExpanded by rememberSaveable { mutableStateOf(false) }
    //var isTouched by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Spacing.Sides, vertical = Spacing.Small
            ), expanded = isExpanded, onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            value = value,
            onValueChange = { },
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = "Select a $placeholder", style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            supportingText = {
                if (!isExpanded && isTouched && isError) {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            })

        ExposedDropdownMenu(
            expanded = isExpanded, onDismissRequest = onDismissRequest
        ) {
            items.forEach { item ->
                DropdownMenuItem(text = {
                    Text(
                        text = item, style = MaterialTheme.typography.bodyMedium
                    )
                }, onClick = {
                    onValueChange(item)
                })
            }
        }
    }
}