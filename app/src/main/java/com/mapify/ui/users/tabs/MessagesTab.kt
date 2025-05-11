package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mapify.model.*
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime

@Composable
fun MessagesTab(
    navigateToConversation: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val allUsers by usersViewModel.users.collectAsState()
    val user = usersViewModel.loadUser(context)!!

    val conversationsList = remember {
        mutableStateListOf<Conversation>().apply {
            if (allUsers.size > 2) {
                addAll(
                    listOf(
                        Conversation(
                            id = "1",
                            participants = listOf(allUsers[1], allUsers[0]),
                            messages = listOf(
                                Message(
                                    id = "msg1",
                                    senderId = allUsers[0].id,
                                    content = "Hi, just checking if there are any updates on the report.",
                                    timestamp = LocalDateTime.now().minusMinutes(5)
                                )
                            ),
                            isRead = mapOf(
                                allUsers[0].id to true,
                                allUsers[1].id to false
                            )
                        ),
                        Conversation(
                            id = "2",
                            participants = listOf(allUsers[2], allUsers[0]),
                            messages = listOf(
                                Message(
                                    id = "msg2",
                                    senderId = allUsers[2].id,
                                    content = "Thanks for your response.",
                                    timestamp = LocalDateTime.now().minusHours(2)
                                )
                            ),
                            isRead = mapOf(
                                allUsers[2].id to true,
                                allUsers[0].id to false
                            )
                        ),
                        Conversation(
                            id = "conv3",
                            participants = listOf(allUsers[1], allUsers[2]),
                            messages = listOf(
                                Message(
                                    id = "msg3",
                                    senderId = allUsers[2].id,
                                    content = "Could you take a look at the file I sent you?",
                                    timestamp = LocalDateTime.now().minusDays(5)
                                )
                            ),
                            isRead = mapOf(
                                allUsers[2].id to true,
                                allUsers[1].id to false
                            )
                        )
                    )
                )
            }
        }
    }

    val filteredConversations by remember {
        derivedStateOf {
            conversationsList.filter { it.participants.any { participant -> participant.id == user.id } }
        }
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
                    markConversationAsRead(conversationsList, conversation.id, user.id)
                    navigateToConversation(conversation.id, true)
                },
                onMarkRead = { markConversationAsRead(conversationsList, conversation.id, user.id) },
                onMarkUnread = { markConversationAsUnread(conversationsList, conversation.id, user.id) },
                onDelete = { conversationsList.removeIf { it.id == conversation.id } },
                recipient = recipient
            )
        }
    }
}

private fun markConversationAsRead(conversations: MutableList<Conversation>, id: String, userId: String) {
    conversations.find { it.id == id }?.apply {
        isRead = isRead.toMutableMap().also { it[userId] = true }
    }
}

private fun markConversationAsUnread(conversations: MutableList<Conversation>, id: String, userId: String) {
    conversations.find { it.id == id }?.apply {
        isRead = isRead.toMutableMap().also { it[userId] = false }
    }
}

//    HandleLocationPermission(
//        onPermissionGranted = {
//
//        }
//    )