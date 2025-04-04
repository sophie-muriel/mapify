package com.mapify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit = {},
    navigateToEditProfile: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_screen_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.profile_back_icon_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = navigateToEditProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.profile_settings_icon_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(id = R.string.profile_avatar_icon_description),
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.profile_greeting),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.Inline * 2))

            GenericTextField(
                value = stringResource(id = R.string.profile_name_value),
                label = stringResource(id = R.string.profile_name_label),
                onValueChange = {},
                isError = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = stringResource(id = R.string.profile_name_icon_description)
                    )
                },
                isSingleLine = true,
                readOnly = true
            )

            GenericTextField(
                value = stringResource(id = R.string.profile_email_value),
                label = stringResource(id = R.string.profile_email_label),
                onValueChange = {},
                isError = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = stringResource(id = R.string.profile_email_icon_description)
                    )
                },
                isSingleLine = true,
                readOnly = true
            )

            GenericTextField(
                value = stringResource(id = R.string.profile_password_value),
                label = stringResource(id = R.string.profile_password_label),
                onValueChange = {},
                isError = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = stringResource(id = R.string.profile_password_icon_description)
                    )
                },
                isSingleLine = true,
                isPassword = true,
                readOnly = true
            )

            GenericTextField(
                value = stringResource(id = R.string.profile_location_value),
                label = stringResource(id = R.string.profile_location_label),
                onValueChange = {},
                isError = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = stringResource(id = R.string.profile_location_icon_description)
                    )
                },
                isSingleLine = true,
                readOnly = true
            )
        }
    }
}
