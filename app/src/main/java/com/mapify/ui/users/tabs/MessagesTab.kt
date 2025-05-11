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
                                    sender = allUsers[0].fullName,
                                    content = "Hi, just checking if there are any updates on the report.",
                                    timestamp = LocalDateTime.now().minusMinutes(5)
                                )
                            ),
                            isRead = false
                        ),
                        Conversation(
                            id = "2",
                            participants = listOf(allUsers[2], allUsers[0]),
                            messages = listOf(
                                Message(
                                    id = "msg2",
                                    sender = allUsers[2].fullName,
                                    content = "Thanks for your response.",
                                    timestamp = LocalDateTime.now().minusHours(2)
                                )
                            ),
                            isRead = true
                        ),
                        Conversation(
                            id = "conv3",
                            participants = listOf(allUsers[1], allUsers[2]),
                            messages = listOf(
                                Message(
                                    id = "msg3",
                                    sender = allUsers[2].fullName,
                                    content = "Could you take a look at the file I sent you?",
                                    timestamp = LocalDateTime.now().minusDays(5)
                                )
                            ),
                            isRead = false
                        )
                    )
                )
            }
        }
    }

    val filteredConversations by remember {
        derivedStateOf {
            conversationsList.filter { user in it.participants }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
    ) {
        items(filteredConversations, key = { it.id }) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClick = {
                    markConversationAsRead(conversationsList, conversation.id)
                    navigateToConversation(conversation.id, true)
                },
                onMarkRead = { markConversationAsRead(conversationsList, conversation.id) },
                onMarkUnread = { markConversationAsUnread(conversationsList, conversation.id) },
                onDelete = { conversationsList.removeIf { it.id == conversation.id } }
            )
        }
    }
}

private fun markConversationAsRead(conversations: MutableList<Conversation>, id: String) {
    conversations.find { it.id == id }?.isRead = true
}

private fun markConversationAsUnread(conversations: MutableList<Conversation>, id: String) {
    conversations.find { it.id == id }?.isRead = false
}

//    HandleLocationPermission(
//        onPermissionGranted = {
//
//        }
//    )