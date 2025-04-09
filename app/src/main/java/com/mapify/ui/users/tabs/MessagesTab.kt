package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.ui.components.MessageItem
import com.mapify.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun MessagesTab(
    navigateToConversation: (Conversation) -> Unit
) {
    val messagesList = listOf(
        Message(
            id = "1",
            sender = "Laura Mejía",
            content = "Hola, quería saber si hay novedades sobre el reporte.",
            timestamp = LocalDateTime.now().minusMinutes(5),
            isRead = false
        ),
        Message(
            id = "2",
            sender = "Carlos Ruiz",
            content = "Gracias por tu respuesta.",
            timestamp = LocalDateTime.now().minusHours(2),
            isRead = true
        ),
        Message(
            id = "3",
            sender = "Andrea Torres",
            content = "¿Podrías revisar el archivo que te envié?",
            timestamp = LocalDateTime.now().minusDays(5),
            isRead = false
        )
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Inline)
    ) {
        items(messagesList) { message ->
            MessageItem(
                sender = message.sender,
                message = message.content,
                time = formatMessageDate(message.timestamp),
                isRead = message.isRead,
                onClick = {
                    val conversation = Conversation(
                        id = message.id,
                        sender = message.sender,
                        messages = listOf(message)
                    )
                    navigateToConversation(conversation)
                }
            )
        }
    }
}

fun formatMessageDate(date: LocalDateTime): String {
    val now = LocalDate.now()
    val messageDate = date.toLocalDate()
    return when {
        messageDate.isEqual(now) -> {
            val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale("es", "CO"))
            date.format(formatter)
        }

        messageDate.isEqual(now.minusDays(1)) -> {
            "Yesterday"
        }

        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "CO"))
            date.format(formatter)
        }
    }
}