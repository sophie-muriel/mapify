package com.mapify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapify.model.Conversation
import com.mapify.model.Participant
import com.mapify.ui.theme.Spacing
import com.mapify.ui.users.tabs.formatNotificationOrMessageDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationItem(
    conversation: Conversation,
    sender: Participant,
    recipient: Participant,
    onClick: () -> Unit,
    onMarkRead: () -> Unit,
    onMarkUnread: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val lastMessage = conversation.messages.maxByOrNull { it.timestamp }
    val lastMessageContent = lastMessage?.content?.let {
        if (lastMessage.senderId == recipient.id) {
            it
        } else {
            "(Me) $it"
        }
    } ?: ""
    val time = lastMessage?.timestamp?.let { formatNotificationOrMessageDate(it) } ?: ""
    val isRead = conversation.isRead[sender.id] ?: false

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { expanded = true }
            )
    ) {
        ElevatedCard(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MessageUserAvatar(
                    name = recipient.name
                )
                ConversationTextSection(
                    name = recipient.name,
                    time = time,
                    content = lastMessageContent,
                    isRead = isRead
                )
            }
        }
        ConversationOptionsMenu(
            expanded = expanded,
            isRead = isRead,
            onDismiss = { expanded = false },
            onMarkRead = {
                expanded = false
                onMarkRead()
            },
            onMarkUnread = {
                expanded = false
                onMarkUnread()
            },
            onDelete = {
                expanded = false
                showDeleteDialog = true
            }
        )
        if (showDeleteDialog) {
            GenericDialog(
                title = "Delete message",
                message = "Are you sure you want to delete this conversation? This action is irreversible.",
                onCloseText = "Cancel",
                onClose = { showDeleteDialog = false },
                onExitText = "Delete",
                onExit = {
                    onDelete()
                    showDeleteDialog = false
                }
            )
        }
    }
}

@Composable
private fun MessageUserAvatar(name: String) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        ProfileIcon(
            fallbackText = name,
            size = 50.dp
        )
    }
}

@Composable
private fun ConversationTextSection(
    name: String,
    time: String,
    content: String,
    isRead: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = Spacing.Small * 3),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (!isRead) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                        color = if (!isRead) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                        lineHeight = 16.sp
                    ),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            if (!isRead) {
                Text(
                    text = "\u2B24", // Bullet unicode
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .align(Alignment.CenterVertically)
                        .offset(y = (-2.5).dp)
                )
            }
        }
    }
}

@Composable
private fun ConversationOptionsMenu(
    expanded: Boolean,
    isRead: Boolean,
    onDismiss: () -> Unit,
    onMarkRead: () -> Unit,
    onMarkUnread: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(x = 165.dp, y = 0.dp)
    ) {
        if (!isRead) {
            DropdownMenuItem(
                text = { Text("Mark as read") },
                onClick = onMarkRead,
                leadingIcon = {
                    Icon(Icons.Default.MarkChatRead, contentDescription = null)
                }
            )
        }
        if (isRead) {
            DropdownMenuItem(
                text = { Text("Mark as unread") },
                onClick = onMarkUnread,
                leadingIcon = {
                    Icon(Icons.Default.MarkChatUnread, contentDescription = null)
                }
            )
        }
        DropdownMenuItem(
            text = {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            },
            onClick = onDelete,
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}