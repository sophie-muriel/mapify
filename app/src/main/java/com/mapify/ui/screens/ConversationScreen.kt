package com.mapify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.ui.components.MenuAction
import com.mapify.ui.components.MinimalDropdownMenu
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    conversation: Conversation,
    navigateBack: () -> Unit
) {

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val messages =
        remember { mutableStateListOf<Message>().apply { addAll(conversation.messages) } }

    val menuItems =
        listOf(
            MenuAction.Simple(
                "Mark as read",
                {
                    Icon(
                        Icons.Default.MarkChatRead,
                        contentDescription = null,
                    )
                }
            ) {
                // mark as read action
            },
            MenuAction.Simple(
                "Mark as unread",
                {
                    Icon(
                        Icons.Default.MarkChatUnread,
                        contentDescription = null
                    )
                }
            ) {
                // mark as unread actions
            },
            MenuAction.Simple(
                "Delete",
                {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            ) {
                // show delete dialog
            }
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = conversation.sender,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.offset(x = 24.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_arrow_icon)
                        )
                    }
                },
                actions = {
                    MinimalDropdownMenu(menuItems)
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Sides),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(
                        message = msg,
                        isMe = msg.sender == "Me",
                        senderName = msg.sender,
                        profileImageUrl = null
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Small, vertical = Spacing.Large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = {
                        Text(
                            text = "Write a message...",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(52.dp)
                        .padding(end = Spacing.Large)
                        .heightIn(min = 52.dp, max = 140.dp),
                    maxLines = 4,
                    singleLine = false,
                    shape = MaterialTheme.shapes.large,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                IconButton(
                    onClick = {
                        if (messageText.text.isNotBlank()) {
                            messages.add(
                                Message(
                                    id = "${messages.size + 1}",
                                    sender = "Me",
                                    content = messageText.text,
                                    timestamp = LocalDateTime.now(),
                                    isRead = true
                                )
                            )
                            messageText = TextFieldValue("")
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: Message,
    isMe: Boolean,
    senderName: String,
    profileImageUrl: String? = null
) {
    val bubbleColor =
        if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = bubbleColor,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .widthIn(max = 280.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Text(
                        text = formatTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "CO"))
    return dateTime.format(formatter)
}