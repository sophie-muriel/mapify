package com.mapify.ui.screens

import android.content.pm.PackageManager
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.GenericTextField
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.theme.Spacing
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.mapify.model.Location
import com.mapify.model.User
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.utils.SharedPreferencesUtils
import fetchUserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import updateCityCountry

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit
) {

    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var locationText by rememberSaveable { mutableStateOf("Loading...") }

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]

    LaunchedEffect(Unit) {
        usersViewModel.resetFoundUser()
        usersViewModel.resetCurrentUser()
        usersViewModel.loadUser(userId)
        userLocation?.let {
            it.updateCityCountry(context)
            locationText = it.toString()
        } ?: run {
            locationText = "Unknown location"
        }
    }

    val user by usersViewModel.user.collectAsState()

    var name by rememberSaveable { mutableStateOf( "") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var nameTouched by rememberSaveable { mutableStateOf(false) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }
    var editMode by rememberSaveable { mutableStateOf(false) }
    var exitDialogVisible by rememberSaveable { mutableStateOf(false) }

    val nameError = nameTouched && name.isBlank()
    val emailError = emailTouched && !(email == "root" || Patterns.EMAIL_ADDRESS.matcher(email).matches())
    val passwordError = passwordTouched && password.length < 6

    LaunchedEffect(user) {
        user?.let {
            name = it.fullName
            email = it.email
            password = it.password
            locationText = it.location.toString()
        }
    }

    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    val locationAccessPermissionGranted = stringResource(id = R.string.location_access_permission_granted)
    val locationAccessPermissionDenied = stringResource(id = R.string.location_access_permission_denied)

    var isRefreshingLocation by rememberSaveable { mutableStateOf(false) }


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
                    if(user!=null){
                        val updatedUser = User(
                            id = user!!.id,
                            fullName = user!!.fullName,
                            email = user!!.email,
                            password = user!!.password,
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
                    if(user!=null){
                        val updatedUser = User(
                            id = user!!.id,
                            fullName = user!!.fullName,
                            email = user!!.email,
                            password = user!!.password,
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
        }
    ) { padding ->
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
                password = password,
                location = locationText,
                isEditMode = editMode,
                onValueChangeName = { name = it; nameTouched = true },
                onValueChangeEmail = { email = it; emailTouched = true },
                onValueChangePassword = { password = it; passwordTouched = true },
                onClickEdit = {
                    user!!.let {
                        val updatedUser = User(
                            id = it.id,
                            fullName = name,
                            email = email,
                            password = password,
                            role = it.role,
                            location = userLocation ?: it.location
                        )
                        usersViewModel.update(user = updatedUser)
                    }
                    nameTouched = false
                    emailTouched = false
                    passwordTouched = false
                    editMode = false
                },
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                onRefreshLocation = { refreshLocation() },
                isRefreshingLocation = isRefreshingLocation
            )

            Spacer(modifier = Modifier.height(Spacing.TopBottomScreen / 2))
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
                    if(user!=null) {
                        name = user!!.fullName
                        email = user!!.email
                        password = user!!.password
                        editMode = false
                    }
                } else {
                    navigateBack()
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
    password: String,
    location: String,
    isEditMode: Boolean,
    onValueChangeName: (String) -> Unit,
    onValueChangeEmail: (String) -> Unit,
    onValueChangePassword: (String) -> Unit,
    onClickEdit: () -> Unit,
    nameError: Boolean,
    emailError: Boolean,
    passwordError: Boolean,
    onRefreshLocation: () -> Unit,
    isRefreshingLocation: Boolean
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
                modifier = Modifier.size(100.dp).padding(bottom = Spacing.Inline),
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Sides),
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
                Icon(Icons.Outlined.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        )

        GenericTextField(
            value = email,
            supportingText = stringResource(id = R.string.email_supporting_text),
            label = stringResource(id = R.string.email_label),
            onValueChange = onValueChangeEmail,
            isError = emailError,
            readOnly = !isEditMode,
            isSingleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        )

        GenericTextField(
            value = password,
            supportingText = stringResource(id = R.string.password_supporting_text),
            label = stringResource(id = R.string.password_label),
            onValueChange = onValueChangePassword,
            isError = passwordError,
            isPassword = true,
            readOnly = !isEditMode,
            isSingleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
                        Icon(Icons.Outlined.Replay, contentDescription = "Refresh location", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        )

        if (isEditMode) {
            Spacer(modifier = Modifier.height(Spacing.Inline * 2))
            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Sides).height(40.dp),
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError,
                onClick = onClickEdit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.save_changes_label),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}