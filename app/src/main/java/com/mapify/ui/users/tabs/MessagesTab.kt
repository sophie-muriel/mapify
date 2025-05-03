package com.mapify.ui.users.tabs

import HandleLocationPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mapify.model.*
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.components.Map
import com.mapify.ui.theme.Spacing
import com.mapify.viewmodel.UsersViewModel
import java.time.LocalDateTime

@Composable
fun MessagesTab(
    navigateToConversation: (String) -> Unit,
    usersViewModel: UsersViewModel
) {

    val allUsers by usersViewModel.users.collectAsState()

    var conversationsList by remember {
        mutableStateOf(
            listOf(
                Conversation(
                    id = "1",
                    recipient = allUsers[0],
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
                    recipient = allUsers[1],
                    messages = listOf(
                        Message(
                            id = "msg2",
                            sender = allUsers[1].fullName,
                            content = "Thanks for your response.",
                            timestamp = LocalDateTime.now().minusHours(2)
                        )
                    ),
                    isRead = true
                ),
                Conversation(
                    id = "conv3",
                    recipient = allUsers[2],
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

    HandleLocationPermission(
        onPermissionGranted = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.Sides),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large)
            ) {
                items(conversationsList, key = { it.id }) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            conversationsList = conversationsList.map {
                                if (it.id == conversation.id) {
                                    it.copy(isRead = true)
                                } else it
                            }
                            navigateToConversation(conversation.id)
                        },
                        onMarkRead = {
                            conversationsList = conversationsList.map {
                                if (it.id == conversation.id) {
                                    it.copy(isRead = true)
                                } else it
                            }
                        },
                        onMarkUnread = {
                            conversationsList = conversationsList.map {
                                if (it.id == conversation.id) {
                                    it.copy(isRead = false)
                                } else it
                            }
                        },
                        onDelete = {
                            conversationsList = conversationsList.filterNot { it.id == conversation.id }
                        }
                    )
                }
            }
        }
    )
}