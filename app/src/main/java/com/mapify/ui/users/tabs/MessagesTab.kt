package com.mapify.ui.users.tabs

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    Log.d("userID viewmodel", userId.toString())


    usersViewModel.loadUser(userId)
    val user = usersViewModel.user.collectAsState().value ?: return

    val conversations by conversationsViewModel.conversations.collectAsState()
    Log.d("MessagesTab", "Conversations list updated: ${conversations.size}")

    var loadingConversationId by remember { mutableStateOf<String?>(null) }

    val filteredConversations = conversations.filter { conversation ->
        user.id in conversation.participants.map { it.id } && conversation.messages.isNotEmpty()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            items(filteredConversations, key = { it.id }) { conversation ->
                val recipient = conversation.participants.firstOrNull { it.id != user.id } ?: user
                ConversationItem(
                    conversation = conversation,
                    onClick = {
                        loadingConversationId = conversation.id
                        conversationsViewModel.markAsRead(conversation.id, user.id)
                        navigateToConversation(conversation.id, true)
                    },
                    onMarkRead = { conversationsViewModel.markAsRead(conversation.id, user.id) },
                    onMarkUnread = { conversationsViewModel.markAsUnread(conversation.id, user.id) },
                    onDelete = {
                        conversationsViewModel.deleteForUser(conversation.id, user.id)
                    },
                    recipient = recipient,
                    user = user
                )
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