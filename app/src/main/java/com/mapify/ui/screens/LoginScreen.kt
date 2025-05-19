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
import androidx.compose.runtime.saveable.rememberSaveable
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResult
import kotlinx.coroutines.delay

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
    navigateToRegistration: () -> Unit,
    navigateToHome: () -> Unit
) {

    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val registerResult by usersViewModel.registerResult.collectAsState()

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
                    usersViewModel.login(email, password)
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

            when (registerResult) {
                null -> {

                }

                is RequestResult.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            (registerResult as RequestResult.Success).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        delay(3000)
                        if (usersViewModel.user.value != null) {
                            navigateToHome()
                        }
                        usersViewModel.resetRegisterResult()
                    }
                }

                is RequestResult.Failure -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            (registerResult as RequestResult.Failure).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        delay(2000)
                        usersViewModel.resetRegisterResult()
                    }
                }

                is RequestResult.Loading -> {
                    LinearProgressIndicator()
                }
            }

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
