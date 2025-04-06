package com.mapify.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.MessageItem
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    navigateToHome: () -> Unit,
    navigateToExplore: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val dummyMessages = List(5) { "Lorem ipsum dolor sit amet, consectetur." }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.messages_label),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateToProfile) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(id = R.string.name_icon_description)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Configuración futura */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings_icon)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                messagesSelected = true,
                navigateToHome = navigateToHome,
                navigateToExplore = navigateToExplore,
                navigateToNotifications = navigateToNotifications,
                navigateToMessages = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* new message action */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_message),
                    contentDescription = stringResource(id = R.string.messages_icon)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)
        ) {
            items(dummyMessages) { message ->
                MessageItem(
                    sender = "List item",
                    message = message
                )
            }
        }
    }
}