package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing

@Composable
fun GenericTextField(
    modifier: Modifier = Modifier,
    value: String,
    supportingText: String = "",
    label: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    leadingIcon: @Composable (() -> Unit)? = null,
    isSingleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                )
        ),
        leadingIcon = leadingIcon,
        singleLine = isSingleLine,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        label = {
            Text(
                text = label, style = MaterialTheme.typography.bodyMedium
            )
        },
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        readOnly = readOnly
    )
}