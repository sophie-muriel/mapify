package com.mapify.ui.screens

import android.util.Patterns
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
import com.mapify.model.Location
import com.mapify.model.Role
import com.mapify.model.User
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.theme.Spacing

@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    isAdmin: Boolean
) {
    val user = User(
        id = if (isAdmin) "2" else "1",
        fullName = if (isAdmin) "Administrator" else "Average User",
        email = if (isAdmin) "admin" else "root",
        password = if (isAdmin) "admin" else "root",
        role = if (isAdmin) Role.ADMIN else Role.CLIENT,
        registrationLocation = Location(
            latitude = 43230.1, longitude = 753948.7, country = "Colombia", city = "Armenia"
        ),
    )

    var name by rememberSaveable { mutableStateOf(user.fullName) }
    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf(user.email) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf(user.password) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }

    val nameError = nameTouched && name.isBlank()
    val emailError =
        emailTouched && !(email == "root" || Patterns.EMAIL_ADDRESS.matcher(email).matches())
    val passwordError = passwordTouched && password.length < 6

    var editMode by rememberSaveable { mutableStateOf(false) }
    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    if ((nameTouched || emailTouched || passwordTouched) && editMode) {
        BackHandler(enabled = true) {
            exitDialogVisible = true
        }
    } else if (editMode){
        BackHandler(enabled = true) {
            editMode = false
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
                    } else if (editMode) {
                        editMode = false
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
                location = user.registrationLocation.toString(),
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
                    user.fullName = name
                    nameTouched = false
                    user.email = email
                    emailTouched = false
                    user.password = password
                    passwordTouched = false
                    editMode = !editMode
                },
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError
            )

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
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
                    name = user.fullName
                    email = user.email
                    password = user.password
                }
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.exit)
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
    onClickEdit: () -> Unit,
    nameError: Boolean,
    emailError: Boolean,
    passwordError: Boolean
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
            supportingText = stringResource(id = R.string.name_supporting_text),
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(id = R.string.name_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = nameError,
            readOnly = !isEditMode,
            isSingleLine = true
        )

        GenericTextField(
            value = oldEmail,
            supportingText = stringResource(id = R.string.email_supporting_text),
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = stringResource(id = R.string.email_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = emailError,
            readOnly = !isEditMode,
            isSingleLine = true
        )

        GenericTextField(
            value = oldPassword,
            supportingText = stringResource(id = R.string.password_supporting_text),
            label = stringResource(id = R.string.password_label),
            onValueChange = onValueChangePassword,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = passwordError,
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
                enabled = oldName.isNotEmpty() && oldEmail.isNotEmpty() && oldPassword.isNotEmpty() && !emailError && !passwordError,
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