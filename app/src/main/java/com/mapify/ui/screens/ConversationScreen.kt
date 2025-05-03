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
import androidx.compose.ui.draw.clip
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
import com.mapify.ui.components.ProfileIcon
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
        User("69", "Barry McCoquiner", "barry.mccoquiner@example.com", "sizedoesntmatter", Role.CLIENT, Location(0.0, 0.0, "USA", "City"), null),
        User("70", "John Smith", "john.smith@example.com", "mockPassword2", Role.CLIENT, Location(0.0, 0.0, "USA", "City"), null),
        User("72", "Alice Johnson", "alice.johnson@example.com", "mockPassword3", Role.CLIENT, Location(0.0, 0.0, "USA", "City"), null),
        User("73", "Mike Cox", "mike.cox@example.com", "mockPassword4", Role.CLIENT, Location(0.0, 0.0, "USA", "City"), null),
        User("74", "Hugh Jass", "hugh.jass@example.com", "mockPassword5", Role.CLIENT, Location(0.0, 0.0, "USA", "City"), null)
    )

    var conversationsList by remember {
        mutableStateOf(
            listOf(
                Conversation("1", allUsers[0], listOf(Message("msg1", allUsers[0].fullName, "Hi, just checking if there are any updates on the report.", LocalDateTime.now().minusMinutes(5))), false),
                Conversation("2", allUsers[1], listOf(Message("msg2", allUsers[1].fullName, "Thanks for your response.", LocalDateTime.now().minusHours(2))), true),
                Conversation("conv3", allUsers[2], listOf(Message("msg3", allUsers[2].fullName, "Could you take a look at the file I sent you?", LocalDateTime.now().minusDays(5))), false)
            )
        )
    }

    val conversation = remember(conversationId) {
        conversationsList.find { it.id == conversationId }!!
    }

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Message>().apply { addAll(conversation.messages) } }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val menuItems = listOf(
        MenuAction.Simple(
            stringResource(id = R.string.delete),
            {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete_icon),
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
                actions = { MinimalDropdownMenu(menuItems) }
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
                .padding(horizontal = Spacing.Large),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(
                        message = msg,
                        isMe = msg.sender == stringResource(id = R.string.me_message),
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
                            text = stringResource(id = R.string.write_a_message),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(end = Spacing.Large)
                        .heightIn(min = 52.dp, max = 140.dp),
                    maxLines = 4,
                    singleLine = false,
                    shape = MaterialTheme.shapes.large,
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                val me = stringResource(id = R.string.me_message)
                IconButton(
                    onClick = {
                        if (messageText.text.isNotBlank()) {
                            messages.add(
                                Message(
                                    id = "${messages.size + 1}",
                                    sender = me,
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
                        contentDescription = stringResource(id = R.string.send),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
    if (showDeleteDialog) {
        GenericDialog(
            title = stringResource(id = R.string.delete_a_message),
            message = stringResource(id = R.string.delete_a_message_confirmation),
            onCloseText = stringResource(id = R.string.cancel),
            onClose = { showDeleteDialog = false },
            onExitText = stringResource(id = R.string.delete),
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
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            ProfileIcon(
                imageUrl = profileImageUrl,
                fallbackText = senderName,
                size = 50.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = if (isMe) 24.dp else 6.dp,
                    bottomEnd = if (isMe) 6.dp else 24.dp
                ),
                color = bubbleColor,
                tonalElevation = 2.dp,
                modifier = Modifier.widthIn(max = 250.dp),
                shadowElevation = 2.dp
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatTime(message.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "CO"))
    return dateTime.format(formatter)
}
