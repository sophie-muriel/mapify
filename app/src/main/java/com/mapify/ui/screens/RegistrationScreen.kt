package com.mapify.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mapify.R
import com.mapify.model.Location
import com.mapify.model.User
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.theme.Spacing
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.RequestResultEffectHandler
import fetchUserLocation
import getLocationName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RegistrationScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val registerResult by usersViewModel.registerResult.collectAsState()

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

    val nameError = nameTouched && (name.isBlank() || name.length > 50)
    val emailError = emailTouched && !(
            (email == "root" || Patterns.EMAIL_ADDRESS.matcher(email).matches()) &&
                    email.length <= 100
            )
    val passwordError = passwordTouched && password.length < 6
    val passwordConfirmationError = passwordConfirmationTouched && passwordConfirmation != password

    val permission = Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationAccessPermissionGranted =
        stringResource(id = R.string.location_access_permission_granted)
    val locationAccessPermissionDenied =
        stringResource(id = R.string.location_access_permission_denied)

    var userLocationLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var userLocationLatitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var locationText by rememberSaveable { mutableStateOf("") }

    var isRefreshingLocation by rememberSaveable { mutableStateOf(false) }

    var isLoading = rememberSaveable { mutableStateOf(false) }
    var dialogVisible by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, locationAccessPermissionGranted, Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.Main).launch {
                val fetchedLocation = fetchUserLocation(context)

                userLocationLongitude = fetchedLocation?.longitude
                userLocationLatitude = fetchedLocation?.latitude
                locationShared = true
                if (userLocationLatitude != null && userLocationLongitude != null) {
                    val locationName =
                        getLocationName(context, userLocationLatitude!!, userLocationLongitude!!)
                    locationText = listOfNotNull(
                        locationName.second,
                        locationName.first,
                        "Latitude: $userLocationLatitude",
                        "Longitude: $userLocationLongitude"
                    ).joinToString(", ")
                }
            }
        } else {
            Toast.makeText(context, locationAccessPermissionDenied, Toast.LENGTH_SHORT).show()
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

    fun refreshLocation() {
        isRefreshingLocation = true
        if (hasPermission) {
            CoroutineScope(Dispatchers.Main).launch {
                val fetchedLocation = fetchUserLocation(context)

                userLocationLatitude = fetchedLocation?.latitude
                userLocationLongitude = fetchedLocation?.longitude

                if (userLocationLatitude != null && userLocationLongitude != null) {
                    val locationName =
                        getLocationName(context, userLocationLatitude!!, userLocationLongitude!!)
                    locationText = listOfNotNull(
                        locationName.second,
                        locationName.first,
                        "Latitude: $userLocationLatitude",
                        "Longitude: $userLocationLongitude"
                    ).joinToString(", ")

                    locationShared = true
                } else {
                    locationText = "Unable to get location"
                }

                isRefreshingLocation = false
            }
        } else {
            isRefreshingLocation = false
            permissionLauncher.launch(permission)
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
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen + 15.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.Large * 3.7f))

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
                location = locationText,
                passwordConfirmation = passwordConfirmation,
                onValueChangePasswordConfirmation = {
                    passwordConfirmation = it
                    passwordConfirmationTouched = true
                },
                passwordConfirmationError = passwordConfirmationError,
                onClickRegister = {
                    userLocationLongitude?.let { lng ->
                        userLocationLatitude?.let { lat ->
                            val newUser = User(
                                fullName = name,
                                email = email,
                                password = password,
                                location = Location(
                                    latitude = lat,
                                    longitude = lng
                                )
                            )
                            usersViewModel.create(newUser)
                        }
                    }
                    locationForm = true
                },
                navigateToLogin = {
                    navigateBack()
                    Handler(Looper.getMainLooper()).postDelayed({
                        resetFields()
                    }, 100)
                },
                onRefreshLocation = { refreshLocation() },
                isRefreshingLocation = isRefreshingLocation,
                isLoading = isLoading.value
            )

            RequestResultEffectHandler(
                requestResult = registerResult,
                context = context,
                isLoading = isLoading,
                onResetResult = { usersViewModel.resetRegisterResult() },
                onNavigate = {
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        resetFields()
                    }, 100)
                    navigateBack()
                }
            )

            if (dialogVisible) {
                GenericDialog(
                    title = dialogTitle,
                    message = dialogMessage,
                    onExit = {
                        dialogVisible = false
                    },
                    onExitText = stringResource(id = R.string.ok),
                )
            }

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
        }
    }
    LaunchedEffect(locationForm) {
        if (locationForm) {
            if (hasPermission) {
                val fetchedLocation = fetchUserLocation(context)

                userLocationLongitude = fetchedLocation?.longitude
                userLocationLatitude = fetchedLocation?.latitude
                locationShared = true
                val locationName =
                    getLocationName(context, userLocationLatitude!!, userLocationLongitude!!)
                locationText =
                    locationName.second?.plus(", ")?.plus(locationName.first)?.plus(", Latitude: ")
                        .plus(userLocationLatitude).plus(", Longitude: ")
                        .plus(userLocationLongitude)
            }
//            else {
//                permissionLauncher.launch(permission)
//            }
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
    location: String,
    onValueChangePassword: (String) -> Unit,
    passwordError: Boolean,
    passwordConfirmation: String,
    onValueChangePasswordConfirmation: (String) -> Unit,
    passwordConfirmationError: Boolean,
    onRefreshLocation: () -> Unit,
    isRefreshingLocation: Boolean,
    onClickRegister: () -> Unit,
    navigateToLogin: () -> Unit,
    isLoading: Boolean
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

        GenericTextField(
            value = location,
            label = stringResource(id = R.string.location),
            onValueChange = {},
            isError = false,
            readOnly = true,
            isSingleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            showTrailingIcon = true,
            trailingIcon = {
                IconButton(
                    onClick = onRefreshLocation,
                    enabled = !isRefreshingLocation
                ) {
                    if (isRefreshingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Replay,
                            contentDescription = "Refresh location",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
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
                    && !emailError && !passwordError && !passwordConfirmationError
                    && location.isNotEmpty() && !isRefreshingLocation
                    && location != "Unable to get location",
            onClick = onClickRegister,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(id = R.string.registration_label),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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