package com.mapify.ui.users.tabs

import android.content.pm.PackageManager
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mapify.R
import com.mapify.model.Location
import com.mapify.model.User
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.RequestResult
import com.mapify.utils.SharedPreferencesUtils
import fetchUserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import updateCityCountry

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProfileTab() {

    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val registerResult by usersViewModel.registerResult.collectAsState()

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    val user by usersViewModel.user.collectAsState()

    var name by rememberSaveable { mutableStateOf("Loading...") }
    var email by rememberSaveable { mutableStateOf("Loading...") }
    var locationText by rememberSaveable { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        usersViewModel.resetFoundUser()
        usersViewModel.resetCurrentUser()
        usersViewModel.loadUser(userId)
        userLocation?.let {
            it.updateCityCountry(context)
            locationText = it.toString()
        } ?: run {
            locationText = "Loading..."
        }
    }

    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }
    var editMode by rememberSaveable { mutableStateOf(false) }
    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    val nameError = nameTouched && (name.isBlank() || name.length > 50)
    val emailError = emailTouched && !(
            (email == "root" || Patterns.EMAIL_ADDRESS.matcher(email).matches()) &&
                    email.length <= 100
            )
    var initialName by rememberSaveable { mutableStateOf("") }
    var initialEmail by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            name = it.fullName
            email = it.email
            initialName = it.fullName
            initialEmail = it.email
            it.location?.updateCityCountry(context)
            locationText = it.location.toString()
        }
    }

    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

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

    var isRefreshingLocation by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var dialogVisible by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    val hasChanges = name != initialName || email != initialEmail
    val isEditEnabled = hasChanges && !nameError && !emailError

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, locationAccessPermissionGranted, Toast.LENGTH_SHORT).show()
            isRefreshingLocation = true
            CoroutineScope(Dispatchers.Main).launch {
                val fetchedLocation = fetchUserLocation(context)

                userLocation = fetchedLocation
                locationText = userLocation?.toString() ?: "Unable to get location"

                userLocation?.let { location ->
                    if (user != null) {
                        val updatedUser = User(
                            id = user!!.id,
                            fullName = user!!.fullName,
                            email = user!!.email,
                            role = user!!.role,
                            location = location
                        )
                        usersViewModel.update(user = updatedUser)
                    }
                }
                isRefreshingLocation = false
            }
        } else {
            Toast.makeText(context, locationAccessPermissionDenied, Toast.LENGTH_SHORT).show()
            isRefreshingLocation = false
        }
    }

    fun refreshLocation() {
        isRefreshingLocation = true
        if (hasPermission) {
            CoroutineScope(Dispatchers.Main).launch {
                val fetchedLocation = fetchUserLocation(context)

                userLocation = fetchedLocation
                locationText = userLocation?.toString() ?: "Unable to get location"

                userLocation?.let { location ->
                    if (user != null) {
                        val updatedUser = User(
                            id = user!!.id,
                            fullName = user!!.fullName,
                            email = user!!.email,
                            role = user!!.role,
                            location = location
                        )
                        usersViewModel.update(user = updatedUser)
                    }
                }
                isRefreshingLocation = false
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    BackHandler(enabled = editMode && (nameTouched || emailTouched || passwordTouched)) {
        exitDialogVisible = true
    }

    BackHandler(enabled = editMode && !(nameTouched || emailTouched || passwordTouched)) {
        editMode = false
    }

    Scaffold() { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))

            ProfileContent(
                name = name,
                email = email,
                location = locationText,
                isEditMode = editMode,
                onValueChangeName = { name = it; nameTouched = true },
                onValueChangeEmail = { email = it; emailTouched = true },
                onClickEdit = {
                    user!!.let {
                        val updatedUser = User(
                            id = it.id,
                            fullName = name,
                            email = email,
                            role = it.role,
                            location = userLocation ?: it.location
                        )
                        usersViewModel.update(user = updatedUser)
                    }
                    nameTouched = false
                    emailTouched = false
                    passwordTouched = false
                },
                nameError = nameError,
                emailError = emailError,
                onRefreshLocation = { refreshLocation() },
                isRefreshingLocation = isRefreshingLocation,
                isLoading = isLoading,
                onClickRecoverPassword = {
                    usersViewModel.sendPasswordReset(user!!.email) { success, error ->
                        if (success) {
                            dialogTitle = context.getString(R.string.email_sent)
                            dialogMessage = context.getString(R.string.check_email_instructions)
                        } else {
                            dialogTitle = context.getString(R.string.email_error)
                            dialogMessage = error ?: context.getString(R.string.unknown_error)
                        }
                        dialogVisible = true
                    }
                },
                isEnabled = isEditEnabled
            )

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))

            when (registerResult) {
                null -> {
                    isLoading = false
                }

                is RequestResult.Success -> {
                    isLoading = false
                    LaunchedEffect(registerResult) {
                        Toast.makeText(
                            context,
                            (registerResult as RequestResult.Success).message,
                            Toast.LENGTH_SHORT
                        ).show()
                        editMode = false
                        delay(2000)
                        usersViewModel.resetRegisterResult()
                    }
                }

                is RequestResult.Failure -> {
                    isLoading = false
                    LaunchedEffect(registerResult) {
                        dialogTitle = "Oops... an error occurred"
                        dialogMessage = (registerResult as RequestResult.Failure).message
                        dialogVisible = true

                        delay(2000)
                        usersViewModel.resetRegisterResult()
                    }
                }

                is RequestResult.Loading -> {
                    isLoading = true
                }
            }

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
        }
    }

    if (exitDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.exit_profile_edit),
            message = stringResource(id = R.string.exit_profile_edit_description),
            onClose = { exitDialogVisible = false },
            onExit = {
                exitDialogVisible = false
                if (editMode) {
                    if (user != null) {
                        name = user!!.fullName
                        email = user!!.email
                        editMode = false
                    }
                } else {
                    // navigateBack()
                }
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.exit)
        )
    }
}

@Composable
fun ProfileContent(
    name: String,
    email: String,
    location: String,
    isEditMode: Boolean,
    onValueChangeName: (String) -> Unit,
    onValueChangeEmail: (String) -> Unit,
    onClickEdit: () -> Unit,
    nameError: Boolean,
    emailError: Boolean,
    onRefreshLocation: () -> Unit,
    isRefreshingLocation: Boolean,
    isLoading: Boolean,
    onClickRecoverPassword: () -> Unit,
    isEnabled: Boolean,
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
                text = stringResource(id = R.string.profile_greeting, name),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(Spacing.Inline * 2))
        } else {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = stringResource(id = R.string.edit_profile_label),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Sides),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.padding(Spacing.Inline))
        }

        GenericTextField(
            value = name,
            supportingText = stringResource(id = R.string.name_supporting_text),
            label = stringResource(id = R.string.name_label),
            onValueChange = onValueChangeName,
            isError = nameError,
            readOnly = !isEditMode,
            isSingleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        GenericTextField(
            value = email,
            supportingText = stringResource(id = R.string.email_supporting_text),
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            isError = emailError,
            readOnly = true,
            isSingleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Outlined.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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

        Spacer(modifier = Modifier.height(Spacing.Inline * 2))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.Sides)
                .height(40.dp),
            enabled = true,
            onClick = onClickRecoverPassword,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Reset password",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (isEditMode) {
            Spacer(modifier = Modifier.height(Spacing.Inline * 2))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Sides)
                    .height(40.dp),
                onClick = onClickEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = isEnabled,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.save_changes_label),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}