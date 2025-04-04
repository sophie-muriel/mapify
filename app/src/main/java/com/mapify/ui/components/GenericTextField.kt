package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.mapify.R
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
    readOnly: Boolean = false,
    showTrailingIcon: Boolean = true,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides, vertical = Spacing.Small)
        ),
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (isPassword && showTrailingIcon) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.password_visibility_description)
                    )
                }
            }
        },
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
        keyboardOptions = if (isPassword) {
            KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        } else keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        label = {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        },
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        readOnly = readOnly
    )
}
