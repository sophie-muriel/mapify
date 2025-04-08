package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mapify.R
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.MessageItem
import com.mapify.ui.theme.Spacing
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTab(
    navController: NavHostController,
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

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(id = R.string.messages_label),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate("profile") // Ajusta si usas rutas con enum
                        }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = stringResource(id = R.string.name_icon_description)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Future settings */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(id = R.string.settings_icon)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Acción para nuevo mensaje */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = stringResource(id = R.string.messages_icon)
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.Sides),
                verticalArrangement = Arrangement.spacedBy(Spacing.Small)
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