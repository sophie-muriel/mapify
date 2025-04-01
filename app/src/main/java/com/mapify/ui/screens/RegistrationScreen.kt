package com.mapify.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.LogoTitle
import com.mapify.ui.theme.MapifyTheme
import com.mapify.ui.theme.Spacing

@Composable
fun RegistrationScreen() {

    var name by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    var passwordConfirmation by rememberSaveable { mutableStateOf("") }
    var passwordConfirmationError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val rootLogin = stringResource(id = R.string.root_login)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // logo + name
                LogoTitle(3.5f)

                Spacer(modifier = Modifier.weight(1f))

                // text form
                RegistrationForm(
                    name,
                    onValueChangeName = {
                        name = it
                        nameError = name.isBlank()
                    },
                    nameError,
                    email,
                    onValueChangeEmail = {
                        email = it
                        emailError =
                            !(email == rootLogin || Patterns.EMAIL_ADDRESS.matcher(email)
                                .matches())
                    },
                    emailError,
                    password,
                    onValueChangePassword = {
                        password = it
                        passwordError = password.length < 6
                    },
                    passwordError,
                    passwordConfirmation,
                    onValueChangePasswordConfirmation = {
                        passwordConfirmation = it
                        passwordConfirmationError = password != it
                    },
                    passwordConfirmationError,
                    onClickRegister = {
                        if (email != rootLogin) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.location_access),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.email_taken),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onClickLogin = { })

            }

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
        }
    }
}

@Composable
fun RegistrationForm(
    name: String,
    onValueChangeName: (String) -> Unit,
    nameError: Boolean,
    email: String,
    onValueChangeEmail: (String) -> Unit,
    emailError: Boolean,
    password: String,
    onValueChangePassword: (String) -> Unit,
    passwordError: Boolean,
    passwordConfirmation: String,
    onValueChangePasswordConfirmation: (String) -> Unit,
    passwordConfirmationError: Boolean,
    onClickRegister: () -> Unit,
    onClickLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(start = 24.dp)
                .align(Alignment.Start),
            text = stringResource(id = R.string.enter_information_label),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        GenericTextField(
            value = name,
            supportingText = stringResource(id = R.string.name_supporting_text),
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            onValidate = {
                nameError
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = stringResource(id = R.string.name_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        GenericTextField(
            value = email,
            supportingText = stringResource(id = R.string.email_supporting_text),
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            onValidate = {
                emailError
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = stringResource(id = R.string.email_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        GenericTextField(
            value = password,
            supportingText = stringResource(id = R.string.password_supporting_text),
            label = stringResource(id = R.string.password_label),
            onValueChange = onValueChangePassword,
            onValidate = {
                passwordError
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isPassword = true
        )

        GenericTextField(
            value = passwordConfirmation,
            supportingText = stringResource(id = R.string.password_confirmation_supporting_text),
            label = stringResource(id = R.string.password_confirmation_label),
            onValueChange = onValueChangePasswordConfirmation,
            onValidate = {
                passwordConfirmationError
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isPassword = true
        )

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp, end = 24.dp
                )
                .height(40.dp),
            enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty() && !emailError && !passwordError && !passwordConfirmationError,
            onClick = onClickRegister,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(
                text = stringResource(id = R.string.login_label),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        TextButton(
            onClick = onClickLogin
        ) {
            Text(
                text = stringResource(id = R.string.login_account),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}