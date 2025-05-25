package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapify.model.Participant
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.SharedPreferencesUtils

@Composable
fun MessagesTab(
    navigateToConversation: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val conversationsViewModel = LocalMainViewModel.current.conversationsViewModel

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    val user by usersViewModel.user.collectAsState()
    var userName by rememberSaveable { mutableStateOf( "") }

    LaunchedEffect(user) {
        usersViewModel.resetFoundUser()
        usersViewModel.resetCurrentUser()
        usersViewModel.loadUser(userId)
        user?.let { userName = it.fullName }
    }

    LaunchedEffect(userId) {
        conversationsViewModel.observeAllConversations(userId!!)
    }

    DisposableEffect(userId) {
        onDispose {
            conversationsViewModel.stopObservingConversations()
        }
    }

    val conversations by conversationsViewModel.conversations.collectAsState()
    var loadingConversationId by remember { mutableStateOf<String?>(null) }

    val filteredConversations = conversations.filter {
        userId in it.participants.map { p -> p.id } && it.messages.isNotEmpty()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            items(filteredConversations, key = { it.id }) { conversation ->
                val recipient = conversation.participants.firstOrNull { it.id != userId }
                recipient?.let {
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            loadingConversationId = conversation.id
                            conversationsViewModel.markAsRead(conversation.id, userId?: "")
                            navigateToConversation(conversation.id, true)
                        },
                        onMarkRead = { conversationsViewModel.markAsRead(conversation.id, userId?: "") },
                        onMarkUnread = { conversationsViewModel.markAsUnread(conversation.id, userId?: "") },
                        onDelete = {
                            conversationsViewModel.deleteForUser(conversation.id, userId?: "")
                        },
                        recipient = it,
                        sender = Participant(userId?: "", userName)
                    )
                }
            }
        }

        if (loadingConversationId != null) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}

//    HandleLocationPermission(
//        onPermissionGranted = {
//
//        }
//    )