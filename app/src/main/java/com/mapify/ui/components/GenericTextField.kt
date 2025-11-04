package com.mapify.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.mapify.R
import com.mapify.ui.theme.Spacing

@Composable
fun GenericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String = "",
    isError: Boolean = false,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    isSingleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
    readOnly: Boolean = false,
    showTrailingIcon: Boolean = true,
    interactionSource: MutableInteractionSource ? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides, vertical = Spacing.Small),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (showTrailingIcon) {
                if (isPassword) {
                    PasswordVisibilityIcon(passwordVisible) { passwordVisible = !passwordVisible }
                } else {
                    trailingIcon?.invoke()
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
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        readOnly = readOnly
    )
}

@Composable
private fun PasswordVisibilityIcon(
    passwordVisible: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = stringResource(id = R.string.password_visibility_description)
        )
    }
}
