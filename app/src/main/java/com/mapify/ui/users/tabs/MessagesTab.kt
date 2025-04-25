package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mapify.model.*
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime

@Composable
fun MessagesTab(
    navigateToConversation: (String) -> Unit
) {

    val allUsers = listOf(
        User(
            id = "69",
            fullName = "Barry McCoquiner",
            email = "barry.mccoquiner@example.com",
            password = "sizedoesntmatter",
            role = Role.CLIENT,
            registrationLocation = Location(0.0, 0.0, "USA", "City"),
            profileImageUrl = null
        ),
        User(
            id = "70",
            fullName = "John Smith",
            email = "john.smith@example.com",
            password = "mockPassword2",
            role = Role.CLIENT,
            registrationLocation = Location(0.0, 0.0, "USA", "City"),
            profileImageUrl = null
        ),
        User(
            id = "72",
            fullName = "Alice Johnson",
            email = "alice.johnson@example.com",
            password = "mockPassword3",
            role = Role.CLIENT,
            registrationLocation = Location(0.0, 0.0, "USA", "City"),
            profileImageUrl = null
        ),
        User(
            id = "73",
            fullName = "Mike Cox",
            email = "mike.cox@example.com",
            password = "mockPassword4",
            role = Role.CLIENT,
            registrationLocation = Location(0.0, 0.0, "USA", "City"),
            profileImageUrl = null
        ),
        User(
            id = "74",
            fullName = "Hugh Jass",
            email = "hugh.jass@example.com",
            password = "mockPassword5",
            role = Role.CLIENT,
            registrationLocation = Location(0.0, 0.0, "USA", "City"),
            profileImageUrl = null
        )
    )

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