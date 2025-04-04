package com.mapify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navigateBack: () -> Unit
) {
    val defaultName = stringResource(id = R.string.profile_name_value)
    val defaultEmail = stringResource(id = R.string.profile_email_value)
    val defaultPassword = stringResource(id = R.string.profile_password_value)
    val defaultLocation = stringResource(id = R.string.profile_location_value)

    var name by rememberSaveable { mutableStateOf(defaultName) }
    var email by rememberSaveable { mutableStateOf(defaultEmail) }
    var password by rememberSaveable { mutableStateOf(defaultPassword) }
    var location by rememberSaveable { mutableStateOf(defaultLocation) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_profile_screen_title),
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

            GenericTextField(
                value = name,
                label = stringResource(id = R.string.profile_name_label),
                onValueChange = { name = it },
                isError = false
            )

            GenericTextField(
                value = email,
                label = stringResource(id = R.string.profile_email_label),
                onValueChange = { email = it },
                isError = false
            )

            GenericTextField(
                value = password,
                label = stringResource(id = R.string.profile_password_label),
                onValueChange = { password = it },
                isError = false,
                isPassword = true
            )

            GenericTextField(
                value = location,
                label = stringResource(id = R.string.profile_location_label),
                onValueChange = { location = it },
                isError = false
            )

            Spacer(modifier = Modifier.height(Spacing.Inline * 2))

            Button(
                onClick = {
                    // Guardar datos o navegar atrás
                    navigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = stringResource(id = R.string.save_changes))
            }
        }
    }
}
