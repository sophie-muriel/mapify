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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.model.Conversation
import com.mapify.model.Location
import com.mapify.model.Message
import com.mapify.model.Role
import com.mapify.model.User
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.MenuAction
import com.mapify.ui.components.MinimalDropdownMenu
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    conversationId: String,
    navigateBack: () -> Unit
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

    val conversation = remember(conversationId) {
        conversationsList.find { it.id == conversationId }!!
    }

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Message>().apply { addAll(conversation.messages) } }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val menuItems =
        listOf(
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
                showDeleteDialog = true
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
                            text = conversation.recipient.fullName,
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
                        profileImageUrl = conversation.recipient.profileImageUrl
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
                                    timestamp = LocalDateTime.now()
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
    if (showDeleteDialog) {
        GenericDialog(
            title = "Delete message",
            message = "Are you sure you want to delete this conversation? This action is irreversible.",
            onCloseText = "Cancel",
            onClose = { showDeleteDialog = false },
            onExitText = "Delete",
            onExit = {
                conversationsList = conversationsList.filterNot { it.id == conversation.id }
                showDeleteDialog = false
                navigateBack()
            }
        )
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