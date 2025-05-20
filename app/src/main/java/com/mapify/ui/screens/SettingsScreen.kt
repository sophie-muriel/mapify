package com.mapify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mapify.ui.components.SimpleTopBar
import com.mapify.R
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.SharedPreferencesUtils

@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToLogin: () -> Unit
) {

    val usersViewModel = LocalMainViewModel.current.usersViewModel

    val context = LocalContext.current

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    Log.d("userID viewmodel", userId.toString())


    val user by usersViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        usersViewModel.loadUser(userId)
    }

    val sendNotifications = remember { mutableStateOf(true) }
    val notificationVibration = remember { mutableStateOf(false) }

    var logoutDialogVisible by rememberSaveable { mutableStateOf(false) }
    var deleteAccountDialogVisible by rememberSaveable { mutableStateOf(false) }
    var accountDeletedDialogVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.settings_screen),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
                actions = false
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            SettingsSection(title = "General") {
                SettingItem(
                    icon = Icons.Filled.Notifications,
                    label = "Send notifications",
                    switchChecked = sendNotifications.value,
                    onSwitchChange = { sendNotifications.value = it }
                )
                SettingItem(
                    icon = Icons.Filled.NotificationsActive,
                    label = "Notification vibration",
                    switchChecked = notificationVibration.value,
                    onSwitchChange = { notificationVibration.value = it }
                )
            }

            Spacer(Modifier.height(Spacing.Large))

            SettingsSection(title = "Account") {
                SettingItem(
                    icon = Icons.Filled.AccountCircle,
                    label = "Edit Profile",
                    onClick = { navigateToProfile() }
                )
                SettingItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Logout",
                    onClick = { logoutDialogVisible = true }
                )
                SettingItem(
                    icon = Icons.Filled.Delete,
                    label = "Delete Account",
                    onClick = { deleteAccountDialogVisible = true }
                )
            }
        }
    }
    if (logoutDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.logout_confirmation),
            message = stringResource(id = R.string.logout_confirmation_description),
            onClose = {
                logoutDialogVisible = false
            },
            onExit = {
                logoutDialogVisible = false
                SharedPreferencesUtils.clearPreference(context)
                usersViewModel.logout()
                navigateToLogin()
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.logout)
        )
    }
    if (deleteAccountDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.delete_account_confirmation),
            message = stringResource(id = R.string.delete_account_confirmation_description),
            onClose = {
                deleteAccountDialogVisible = false
            },
            onExit = {
                deleteAccountDialogVisible = false
                accountDeletedDialogVisible = true
            },
            onCloseText = stringResource(id = R.string.cancel),
            onExitText = stringResource(id = R.string.delete_account)
        )
    }
    if (accountDeletedDialogVisible) {
        GenericDialog(
            title = stringResource(id = R.string.account_deleted),
            message = stringResource(id = R.string.account_deleted_message),
            onExit = {
                accountDeletedDialogVisible = false
                usersViewModel.delete(user!!.id)
                SharedPreferencesUtils.clearPreference(context)
                usersViewModel.logout()
                usersViewModel.resetRegisterResult()
                navigateToLogin()
            },
            onExitText = stringResource(id = R.string.ok)
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = Spacing.Small)
        )
        content()
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)? = null,
    switchChecked: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null
) {
    val isToggle = switchChecked != null && onSwitchChange != null
    val rowHeight = 40.dp

    val rowContent: @Composable RowScope.() -> Unit = {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.Large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = Spacing.Large)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isToggle) {
            Switch(
                checked = switchChecked!!,
                onCheckedChange = onSwitchChange,
                modifier = Modifier
                    .scale(0.85f)
                    .padding(end = Spacing.Small)
            )
        }
    }

    if (isToggle) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rowContent()
        }
    } else {
        Button(
            onClick = onClick ?: {},
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            contentPadding = PaddingValues(horizontal = 0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = null
        ) {
            rowContent()
        }
    }
}