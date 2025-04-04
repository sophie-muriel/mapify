package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R

@Composable
fun BottomNavigationBar(
    homeSelected: Boolean = false,
    navigateToHome: () -> Unit = {},
    searchSelected: Boolean = false,
    navigateToExplore: () -> Unit = {},
    notificationsSelected: Boolean = false,
    navigateToNotifications: () -> Unit = {},
    messagesSelected: Boolean = false,
    navigateToMessages: () -> Unit = {},
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = stringResource(id = R.string.home_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            selected = homeSelected,
            onClick = {
                if(!homeSelected){
                    navigateToHome()
                }
            },
        )
        NavigationBarItem(icon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = stringResource(id = R.string.search_icon),
                tint = MaterialTheme.colorScheme.primary
            )
        }, selected = searchSelected,
            onClick = {
                if(!searchSelected){
                    navigateToExplore()
                }
            }
        )
        NavigationBarItem(icon = {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = stringResource(id = R.string.notifications_icon),
                tint = MaterialTheme.colorScheme.primary
            )
        }, selected = notificationsSelected,
            onClick = {
                if(!notificationsSelected){
                    navigateToNotifications()
                }
            }
        )
        NavigationBarItem(icon = {
            Icon(
                Icons.Filled.QuestionAnswer,
                contentDescription = stringResource(id = R.string.messages_icon),
                tint = MaterialTheme.colorScheme.primary
            )
        }, selected = messagesSelected,
            onClick = {
                if(!messagesSelected){
                    navigateToMessages()
                }
            }
        )
    }
}