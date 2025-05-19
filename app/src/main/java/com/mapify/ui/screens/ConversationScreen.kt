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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.model.Message
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.components.MenuAction
import com.mapify.ui.components.MinimalDropdownMenu
import com.mapify.ui.components.ProfileIcon
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val COLOMBIA_LOCALE = Locale("es", "CO")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    id: String,
    isConversation: Boolean,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val conversationsViewModel = LocalMainViewModel.current.conversationsViewModel

    usersViewModel.loadUser(context)
    val user = usersViewModel.user.value ?: return

    val conversation = remember {
        if (isConversation) {
            conversationsViewModel.find(id) ?: error("Conversation not found.")
        } else {
            usersViewModel.findById(id)
            conversationsViewModel.createConversation(
                user,
                usersViewModel.foundUser.value!!
            )
        }
    }

    conversationsViewModel.getMessages(conversation.id)
    val messages by conversationsViewModel.messages.collectAsState()

    val recipient = remember(conversation) {
        conversation.participants.first { it != user }
    }

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = recipient.fullName,
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
                    MinimalDropdownMenu(
                        listOf(
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
                    )
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
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Large)
                    .padding(bottom = Spacing.Large),
                verticalArrangement = Arrangement.spacedBy(Spacing.Large),
                reverseLayout = true
            ) {
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        isMe = msg.senderId == user.id,
                        senderName = if (msg.senderId == user.id) user.fullName else recipient.fullName,
                        profileImageUrl = recipient.profileImageUrl
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = Spacing.Large)
                        .padding(horizontal = Spacing.Large),
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
                    IconButton(
                        onClick = {
                            if (messageText.text.isNotBlank()) {
                                conversationsViewModel.sendMessage(
                                    conversation.id,
                                    user.id,
                                    messageText.text
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
    }
    if (showDeleteDialog) {
        GenericDialog(
            title = stringResource(id = R.string.delete_a_message),
            message = stringResource(id = R.string.delete_a_message_confirmation),
            onCloseText = stringResource(id = R.string.cancel),
            onClose = { showDeleteDialog = false },
            onExitText = stringResource(id = R.string.delete),
            onExit = {
                conversationsViewModel.deleteForUser(conversation.id, user.id)
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
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", COLOMBIA_LOCALE)
    return dateTime.format(formatter)
}
