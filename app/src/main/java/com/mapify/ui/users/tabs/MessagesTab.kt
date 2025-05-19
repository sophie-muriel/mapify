package com.mapify.ui.users.tabs

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing

@Composable
fun MessagesTab(
    navigateToConversation: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val conversationsViewModel = LocalMainViewModel.current.conversationsViewModel
    usersViewModel.loadUser(context)
    val user = usersViewModel.user.value ?: return

    val conversations by conversationsViewModel.conversations.collectAsState()
    Log.d("MessagesTab", "Conversations list updated: ${conversations.size}")

    val filteredConversations = conversations.filter { conversation ->
        user.id in conversation.participants.map { it.id } && conversation.messages.isNotEmpty()
    }

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
                    conversationsViewModel.markAsRead(conversation.id, user.id)
                    navigateToConversation(conversation.id, true)
                },
                onMarkRead = { conversationsViewModel.markAsRead(conversation.id, user.id) },
                onMarkUnread = { conversationsViewModel.markAsUnread(conversation.id, user.id) },
                onDelete = {
                    conversationsViewModel.deleteForUser(conversation.id, user.id) },
                recipient = recipient,
                user = user
            )
        }
    }
}

//    HandleLocationPermission(
//        onPermissionGranted = {
//
//        }
//    )