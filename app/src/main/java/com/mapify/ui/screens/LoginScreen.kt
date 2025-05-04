package com.mapify.ui.screens

import android.app.Activity
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.LogoTitle
import com.mapify.ui.theme.Spacing
import com.mapify.utils.SharedPreferencesUtils
import com.mapify.viewmodel.UsersViewModel
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun SetSoftInputModePan() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        val window = activity?.window
        val originalMode = window?.attributes?.softInputMode

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        onDispose {
            originalMode?.let { window.setSoftInputMode(it) }
        }
    }
}

@Composable
fun LoginScreen(
    usersViewModel: UsersViewModel,
    navigateToRegistration: () -> Unit,
    navigateToHome: () -> Unit
) {
    SetSoftInputModePan()
    val context = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var recoveryEmail by rememberSaveable { mutableStateOf("") }
    var recoveryEmailTouched by rememberSaveable { mutableStateOf(false) }

    val recoveryEmailError = recoveryEmailTouched && !Patterns.EMAIL_ADDRESS.matcher(recoveryEmail).matches()

    fun resetFields() {
        email = ""
        password = ""
    }

    fun resetRecoveryFields() {
        recoveryEmail = ""
        recoveryEmailTouched = false
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
            LogoTitle(2f)
            Spacer(modifier = Modifier.height(Spacing.Large * 7.8f))

            LoginForm(
                email = email,
                password = password,
                onValueChangeEmail = { email = it },
                onValueChangePassword = { password = it },
                onClickLogin = {
                    val user = usersViewModel.login(email, password)
                    if (user == null) {
                        Toast.makeText(context, R.string.incorrect_credentials, Toast.LENGTH_SHORT).show()
                    } else {
                        SharedPreferencesUtils.savePreference(context, user.id, user.role)
                        navigateToHome()
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            resetFields()
                        }, 200)
                    }
                },
                onClickRegistration = {
                    navigateToRegistration()
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ resetFields() }, 100)
                },
                recoveryEmail = recoveryEmail,
                onValueChangeRecoveryEmail = {
                    recoveryEmail = it
                    recoveryEmailTouched = true
                },
                onClickRecovery = {
                    Toast.makeText(context, R.string.recovery_email_sent, Toast.LENGTH_SHORT).show()
                },
                recoveryEmailError = recoveryEmailError,
                resetRecoveryFields = { resetRecoveryFields() }
            )

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
        }
    }
}

@Composable
fun LoginForm(
    email: String,
    password: String,
    onValueChangeEmail: (String) -> Unit,
    onValueChangePassword: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickRegistration: () -> Unit,
    recoveryEmail: String,
    onValueChangeRecoveryEmail: (String) -> Unit,
    onClickRecovery: () -> Unit,
    recoveryEmailError: Boolean,
    resetRecoveryFields: () -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GenericTextField(
            value = email,
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            isError = false,
            leadingIcon = {
                Icon(Icons.Rounded.Email, contentDescription = stringResource(R.string.email_icon_description), tint = MaterialTheme.colorScheme.primary)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        GenericTextField(
            value = password,
            label = stringResource(id = R.string.password_label),
            onValueChange = onValueChangePassword,
            isError = false,
            leadingIcon = {
                Icon(Icons.Rounded.Lock, contentDescription = stringResource(R.string.password_icon_description), tint = MaterialTheme.colorScheme.primary)
            },
            isPassword = true
        )

        TextButton(
            modifier = Modifier.padding(end = 36.dp).align(Alignment.End),
            onClick = { dialogVisible = true }
        ) {
            Text(stringResource(id = R.string.forgot_password), style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(Spacing.Inline))

        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Sides).height(40.dp),
            enabled = email.isNotBlank() && password.isNotBlank(),
            onClick = onClickLogin,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(stringResource(id = R.string.login_label), style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(Spacing.Inline))

        TextButton(onClick = onClickRegistration) {
            Text(stringResource(id = R.string.register_account), style = MaterialTheme.typography.labelSmall)
        }
    }

    if (dialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.forgot_password),
            message = stringResource(id = R.string.forgot_password_description),
            onClose = {
                resetRecoveryFields()
                dialogVisible = false
            },
            onExit = {
                onClickRecovery()
                resetRecoveryFields()
                dialogVisible = false
            },
            onCloseText = stringResource(id = R.string.back),
            onExitText = stringResource(id = R.string.send_email_recovery_link),
            textField = {
                GenericTextField(
                    value = recoveryEmail,
                    supportingText = stringResource(id = R.string.email_supporting_text),
                    label = stringResource(id = R.string.email_label),
                    onValueChange = onValueChangeRecoveryEmail,
                    isError = recoveryEmailError,
                    leadingIcon = {
                        Icon(Icons.Rounded.Email, contentDescription = stringResource(id = R.string.email_icon_description), tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }
        )
    }
}
