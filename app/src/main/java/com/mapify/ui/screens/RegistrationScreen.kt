package com.mapify.ui.screens

import android.content.pm.PackageManager
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mapify.R
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.LogoTitle
import com.mapify.ui.theme.Spacing

@Composable
fun RegistrationScreen(
    navigateBack: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }
    var passwordConfirmation by rememberSaveable { mutableStateOf("") }
    var passwordConfirmationTouched by rememberSaveable { mutableStateOf(false) }

    var locationShared by rememberSaveable { mutableStateOf(false) }
    var locationForm by rememberSaveable { mutableStateOf(false) }
    val location = "Colombia" // fixed for now, change when location access is explained in class

    val context = LocalContext.current

    val nameError = nameTouched && name.isBlank()
    val emailError =
        emailTouched && !(email == "root" || Patterns.EMAIL_ADDRESS.matcher(email).matches())
    val passwordError = passwordTouched && password.length < 6
    val passwordConfirmationError = passwordConfirmationTouched && passwordConfirmation != password

    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if(hasPermission){
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetFields() {
        name = ""
        nameTouched = false
        email = ""
        emailTouched = false
        password = ""
        passwordTouched = false
        passwordConfirmation = ""
        passwordConfirmationTouched = false
    }

    if (locationForm) {
        BackHandler(enabled = true) {
            locationForm = false
        }
    }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // registration form first part
            if (!locationForm) {
                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen + 15.dp))

                // logo + name
                LogoTitle(3.5f)

                Spacer(modifier = Modifier.height(Spacing.Large * 2.8f))

                RegistrationForm(
                    name = name,
                    onValueChangeName = {
                        name = it
                        nameTouched = true
                    },
                    nameError = nameError,
                    email = email,
                    onValueChangeEmail = {
                        email = it
                        emailTouched = true
                    },
                    emailError = emailError,
                    password = password,
                    onValueChangePassword = {
                        password = it
                        passwordTouched = true
                    },
                    passwordError = passwordError,
                    passwordConfirmation = passwordConfirmation,
                    onValueChangePasswordConfirmation = {
                        passwordConfirmation = it
                        passwordConfirmationTouched = true
                    },
                    passwordConfirmationError = passwordConfirmationError,
                    onClickRegister = {
                        if (email != "root") {
                            locationForm = true
                        } else {
                            Toast.makeText(
                                context, context.getString(R.string.email_taken), Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    navigateToLogin = {
                        navigateBack()
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            resetFields()
                        }, 100)
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
            } else { // registration form part 2
                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mapify_dark), // placeholder
                        contentDescription = stringResource(id = R.string.location_icon_description),
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .aspectRatio(2f)
                    )
                }

                Text(
                    text = stringResource(id = R.string.enable_location_title_message),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(Spacing.Large))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Spacing.Sides * 2,
                            vertical = if (locationShared) (Spacing.Large + 4.dp) else 0.dp
                        )
                        .align(Alignment.CenterHorizontally),
                    text = if (locationShared)
                        stringResource(id = R.string.location_enabled, location)
                    else
                        stringResource(id = R.string.enable_location_access_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Spacing.Large * 18.1f))

                ConfirmLocationForm(
                    locationShared = locationShared,
                    onClickConfirmLocation = {
                        if (locationShared) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.registration_successful),
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateBack()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                resetFields()
                            }, 100)
                        } else {
                            locationShared = true
                        }
                    }
                )
            }
        }
    }
    if(locationShared){
        LaunchedEffect(Unit) {
            if(!hasPermission){
                permissionLauncher.launch(permission)
            }
        }
    }else{
        locationShared = false
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
    navigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(start = Spacing.Sides)
                .align(Alignment.Start),
            text = stringResource(id = R.string.enter_information_label),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.padding(Spacing.Small))

        GenericTextField(
            value = name,
            supportingText = stringResource(id = R.string.name_supporting_text),
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            isError = nameError,
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
            isError = emailError,
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
            isError = passwordError,
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
            isError = passwordConfirmationError,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = stringResource(id = R.string.password_icon_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            isPassword = true,
            showTrailingIcon = false
        )

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp
                )
                .height(40.dp),
            enabled = name.isNotEmpty() && email.isNotEmpty()
                    && password.isNotEmpty() && passwordConfirmation.isNotEmpty()
                    && !emailError && !passwordError && !passwordConfirmationError,
            onClick = onClickRegister,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(
                text = stringResource(id = R.string.registration_label),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(Spacing.Inline))

        TextButton(onClick = navigateToLogin) {
            Text(
                text = stringResource(id = R.string.login_account),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun ConfirmLocationForm(
    locationShared: Boolean, onClickConfirmLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = true,
            onClick = onClickConfirmLocation,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            Text(
                text = if (locationShared)
                    stringResource(id = R.string.finish_registration)
                else stringResource(id = R.string.enable_location_access),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Spacer(modifier = Modifier.padding(Spacing.Inline))

    TextButton(
        onClick = {},
        enabled = false
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.labelSmall
        )
    }

    Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
}