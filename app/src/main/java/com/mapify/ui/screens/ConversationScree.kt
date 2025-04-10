package com.mapify.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.ui.theme.Spacing
import java.time.format.DateTimeFormatter

@Composable
fun ConversationScreen(
    conversation: Conversation,
    navigateBack: () -> Unit
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        TopAppBar(
            title = { Text(conversation.sender) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Spacing.Sides),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversation.messages) { msg ->
                ChatBubble(
                    message = msg,
                    isMe = msg.sender != conversation.sender // simplificado
                )
            }
        }

        // Input box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Inline),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* lógica de enviar */ }) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isMe: Boolean) {
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(color = bubbleColor, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = textColor
            )
            Text(
                text = message.timestamp.format(DateTimeFormatter.ofPattern("hh:mm a")),
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
