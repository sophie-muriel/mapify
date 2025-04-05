package com.mapify.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateToHome: () -> Unit
) {
    // TODO: take variables and replace old variables with them maybe?

    val oldName = "root"
    val oldEmail = "root"
    val oldPassword = "000000"
    val location = "4°32;30.1;N 75°39;48.7;W"

    var name by rememberSaveable { mutableStateOf(oldName) }
    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf(oldEmail) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf(oldPassword) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }

    val isKeyboardActive = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var editMode by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!isKeyboardActive) {
                TopAppBar(modifier = Modifier.padding(horizontal = Spacing.Small), title = {
                    Text(
                        text = stringResource(id = R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateToHome()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_arrow_icon)
                        )
                    }
                }, actions = {
                    //TODO: add popup to confirm discarding changes (if fields touched) AND saved changes (if fields touched)
                    IconButton(
                        onClick = {
                            if (editMode) { // temporary (i think)
                                name = oldName
                                email = oldEmail
                                password = oldPassword
                            }
                            editMode = !editMode
                        }) {
                        Icon(
                            imageVector = if (editMode) Icons.Outlined.Close else Icons.Outlined.Edit,
                            contentDescription = stringResource(
                                id = if (editMode) R.string.close_icon_description else R.string.edit_icon_description
                            )
                        )
                    }
                })
            }
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))

            if (!editMode) {
                ProfileInformation(oldName, oldEmail, oldPassword, location)
            } else {
                ProfileEdit(name, email, password, location, onValueChangeName = {
                    name = it
                    nameTouched = true
                }, onValueChangeEmail = {
                    email = it
                    emailTouched = true
                }, onValueChangePassword = {
                    password = it
                    passwordTouched = true
                }, onClickEdit = { editMode = !editMode })
            }

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))
        }
    }
}

@Composable
fun ProfileInformation(
    name: String, email: String, password: String, location: String // ?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = stringResource(id = R.string.name_icon_description),
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = Spacing.Inline),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(id = R.string.profile_greeting, name),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(Spacing.Inline * 2))

        GenericTextField(
            value = name,
            label = stringResource(id = R.string.name_label),
            onValueChange = {},
            isError = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(id = R.string.name_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isSingleLine = true,
            readOnly = true
        )

        GenericTextField(
            value = email,
            label = stringResource(id = R.string.email_label),
            onValueChange = {},
            isError = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = stringResource(id = R.string.email_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isSingleLine = true,
            readOnly = true
        )

        GenericTextField(
            value = password,
            label = stringResource(id = R.string.password_label),
            onValueChange = {},
            isError = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isSingleLine = true,
            isPassword = true,
            readOnly = true
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
            isSingleLine = true,
            readOnly = true
        )
    }
}

@Composable
fun ProfileEdit(
    name: String,
    email: String,
    password: String,
    location: String, // ?
    onValueChangeName: (String) -> Unit,
    onValueChangeEmail: (String) -> Unit,
    onValueChangePassword: (String) -> Unit,
    onClickEdit: () -> Unit // show popup to confirm changes or something idk
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        GenericTextField(
            value = name,
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(id = R.string.name_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = false
        )

        GenericTextField(
            value = email,
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = stringResource(id = R.string.email_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isError = false
        )

        GenericTextField(
            value = password,
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
            isPassword = true
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
            isSingleLine = true,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(Spacing.Inline * 2))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = true, // TODO: check for field errors ahahahahahaha
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