package com.mapify.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.DiscardChangesDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.theme.Spacing

@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    isAdmin: Boolean
) {
    var oldName by rememberSaveable { mutableStateOf(if (isAdmin) "Administrator" else "Average User") }
    var oldEmail by rememberSaveable { mutableStateOf(if (isAdmin) "admin" else "root") }
    var oldPassword by rememberSaveable { mutableStateOf(if (isAdmin) "admin" else "root") }
    val location = "4°32;30.1;N 75°39;48.7;W" // how to update location? beats me!

    var name by rememberSaveable { mutableStateOf(oldName) }
    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf(oldEmail) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf(oldPassword) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }

    var editMode by rememberSaveable { mutableStateOf(false) }

    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    if (nameTouched || emailTouched || passwordTouched) {
        BackHandler(enabled = true) {
            exitDialogVisible = true
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.edit_profile_label),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    if (editMode && (nameTouched || emailTouched || passwordTouched)) {
                        exitDialogVisible = true
                    } else {
                        navigateBack()
                    }
                },
                actions = !editMode,
                firstActionIconVector = Icons.Outlined.Edit,
                firstActionIconDescription = stringResource(id = R.string.edit_icon_description),
                firstOnClickAction = {
                    if (editMode && (nameTouched || emailTouched || passwordTouched)) {
                        exitDialogVisible = true
                    } else {
                        editMode = !editMode
                    }
                }
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))

            ProfileContent(
                oldName = name,
                oldEmail = email,
                oldPassword = password,
                location = location,
                isEditMode = editMode,
                onValueChangeName = {
                    name = it
                    nameTouched = true
                },
                onValueChangeEmail = {
                    email = it
                    emailTouched = true
                },
                onValueChangePassword = {
                    password = it
                    passwordTouched = true
                },
                onClickEdit = {
                    oldName = name
                    nameTouched = false
                    oldEmail = email
                    emailTouched = false
                    oldPassword = password
                    passwordTouched = false
                    editMode = !editMode
                }
            )

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))
        }
    }

    if (exitDialogVisible) {
        DiscardChangesDialog(
            title = stringResource(id = R.string.exit_profile_edit),
            message = stringResource(id = R.string.exit_profile_edit_description),
            onClose = {
                exitDialogVisible = false
            },
            onExit = {
                if (!editMode) {
                    exitDialogVisible = false
                    navigateBack()
                } else {
                    exitDialogVisible = false
                    editMode = false

                    name = oldName
                    email = oldEmail
                    password = oldPassword
                }
            }
        )
    }
}

@Composable
fun ProfileContent(
    oldName: String,
    oldEmail: String,
    oldPassword: String,
    location: String,
    isEditMode: Boolean,
    onValueChangeName: (String) -> Unit,
    onValueChangeEmail: (String) -> Unit,
    onValueChangePassword: (String) -> Unit,
    onClickEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isEditMode) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = stringResource(id = R.string.name_icon_description),
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = Spacing.Inline),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.profile_greeting, oldName),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(Spacing.Inline * 2))
        } else {
            Spacer(Modifier.height(100.dp))

            Text(
                text = stringResource(id = R.string.edit_profile_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Sides),
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.padding(Spacing.Inline))
        }

        GenericTextField(
            value = oldName,
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(id = R.string.name_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = false,
            readOnly = !isEditMode,
            isSingleLine = true
        )

        GenericTextField(
            value = oldEmail,
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = stringResource(id = R.string.email_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = false,
            readOnly = !isEditMode,
            isSingleLine = true
        )

        GenericTextField(
            value = oldPassword,
            label = stringResource(id = R.string.password_label),
            onValueChange = onValueChangePassword,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = false,
            isPassword = true,
            readOnly = !isEditMode,
            isSingleLine = true
        )

        GenericTextField(
            value = location,
            label = stringResource(id = R.string.location),
            onValueChange = {},
            isError = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = stringResource(id = R.string.location_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            readOnly = true,
            isSingleLine = true
        )

        if (isEditMode) {
            Spacer(modifier = Modifier.height(Spacing.Inline * 2))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Sides)
                    .height(40.dp),
                enabled = true,
                onClick = { onClickEdit() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes_label),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}