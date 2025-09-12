package com.mapify.ui.users.tabs

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.mapify.R
import com.mapify.model.Participant
import com.mapify.ui.components.ConversationItem
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.RequestResult
import com.mapify.utils.SharedPreferencesUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessagesTab(
    navigateToConversation: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val conversationsViewModel = LocalMainViewModel.current.conversationsViewModel
    val coroutineScope = rememberCoroutineScope()

    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    val firebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
    Log.d("MessagesTab", "SharedPrefs userId: $userId, Firebase userId: $firebaseUserId")
    if (userId == null || userId != firebaseUserId) {
        Log.e("MessagesTab", "Invalid or missing user ID")
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Error: Invalid or missing user ID",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(Spacing.Large)
            )
        }
        return
    }

    val userResult by usersViewModel.userResult.collectAsState()
    var userName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(userId) {
        usersViewModel.loadUser(userId)
    }

    LaunchedEffect(userResult) {
        when (userResult) {
            is RequestResult.Success -> {
                userName = usersViewModel.user.value?.fullName ?: ""
                Log.d("MessagesTab", "User loaded successfully, fullName: $userName")
            }
            is RequestResult.Failure -> {
                Toast.makeText(context, (userResult as RequestResult.Failure).message, Toast.LENGTH_SHORT).show()
                Log.e("MessagesTab", "User load error: ${(userResult as RequestResult.Failure).message}")
            }
            is RequestResult.Loading -> {
                Log.d("MessagesTab", "Loading user...")
            }
            null -> {
                Log.d("MessagesTab", "Initial userResult state")
            }
        }
    }

    LaunchedEffect(userId) {
        conversationsViewModel.observeAllConversations(userId)
    }

    DisposableEffect(userId) {
        onDispose {
            conversationsViewModel.stopObservingConversations()
        }
    }

    val conversations by conversationsViewModel.conversations.collectAsState()
    val conversationResult by conversationsViewModel.conversationResult.collectAsState()
    var loadingConversationId by remember { mutableStateOf<String?>(null) }
    var deletingConversationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(conversationResult) {
        when (conversationResult) {
            is RequestResult.Success -> {
                Log.d("MessagesTab", "Conversation operation succeeded: ${(conversationResult as RequestResult.Success).message}")
                Toast.makeText(context, (conversationResult as RequestResult.Success).message, Toast.LENGTH_SHORT).show()
                deletingConversationId = null
                loadingConversationId = null
            }
            is RequestResult.Failure -> {
                val message = (conversationResult as RequestResult.Failure).message
                Log.e("MessagesTab", "Conversation operation failed: $message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                deletingConversationId = null
                loadingConversationId = null
            }
            is RequestResult.Loading -> {
                Log.d("MessagesTab", "Conversation operation in progress...")
            }
            null -> {
                Log.d("MessagesTab", "Initial conversationResult state")
            }
        }
    }

    // Timeout for spinner to prevent infinite loading
    LaunchedEffect(deletingConversationId) {
        if (deletingConversationId != null) {
            delay(10000) // 10-second timeout
            if (deletingConversationId != null) {
                Log.w("MessagesTab", "Deletion timeout for conversation $deletingConversationId")
                Toast.makeText(context, "Deletion timed out, please try again", Toast.LENGTH_SHORT).show()
                deletingConversationId = null
            }
        }
    }

    LaunchedEffect(conversations) {
        Log.d("MessagesTab", "Conversations updated, size: ${conversations.size}, IDs: ${conversations.map { it.id }}")
    }

    val filteredConversations = conversations.filter {
        userId in it.participants.map { p -> p.id } && !it.deletedFor.contains(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (userResult) {
            is RequestResult.Success -> {
                if (filteredConversations.isEmpty() && loadingConversationId == null && deletingConversationId == null) {
                    Text(
                        text = stringResource(id = R.string.no_conversations),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(Spacing.Large)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Spacing.Sides),
                        verticalArrangement = Arrangement.spacedBy(Spacing.Large)
                    ) {
                        items(filteredConversations, key = { it.id }) { conversation ->
                            val recipient = conversation.participants.firstOrNull { it.id != userId }
                            if (recipient != null && userName.isNotEmpty()) {
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = {
                                        Log.d("MessagesTab", "Navigating to conversation: ${conversation.id}")
                                        loadingConversationId = conversation.id
                                        conversationsViewModel.markAsRead(conversation.id, userId)
                                        navigateToConversation(conversation.id, true)
                                    },
                                    onMarkRead = {
                                        Log.d("MessagesTab", "Marking conversation as read: ${conversation.id}")
                                        conversationsViewModel.markAsRead(conversation.id, userId)
                                    },
                                    onMarkUnread = {
                                        Log.d("MessagesTab", "Marking conversation as unread: ${conversation.id}")
                                        conversationsViewModel.markAsUnread(conversation.id, userId)
                                    },
                                    onDelete = {
                                        Log.d("MessagesTab", "Initiating deletion of conversation: ${conversation.id}")
                                        deletingConversationId = conversation.id
                                        coroutineScope.launch {
                                            conversationsViewModel.deleteForUser(conversation.id, userId)
                                        }
                                    },
                                    recipient = recipient,
                                    sender = Participant(userId, userName)
                                )
                            } else {
                                Log.w("MessagesTab", "Skipping conversation ${conversation.id}: recipient=$recipient, userName=$userName")
                            }
                        }
                    }
                }
            }
            is RequestResult.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
            is RequestResult.Failure -> {
                Text(
                    text = (userResult as RequestResult.Failure).message,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Spacing.Large)
                )
            }
            null -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }

        if (loadingConversationId != null || deletingConversationId != null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}