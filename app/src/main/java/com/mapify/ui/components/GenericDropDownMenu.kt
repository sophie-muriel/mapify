package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides, vertical = Spacing.Small)
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
                    text = "Select a $placeholder",
                    style = MaterialTheme.typography.bodyMedium
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
            }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismissRequest
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = { onValueChange(item) }
                )
            }
        }
    }
}