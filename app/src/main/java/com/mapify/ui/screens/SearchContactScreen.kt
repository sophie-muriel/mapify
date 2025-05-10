package com.mapify.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.model.Conversation
import com.mapify.model.Message
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.SearchUserItem
import com.mapify.ui.components.SimpleTopBar
import com.mapify.viewmodel.UsersViewModel
import java.time.LocalDateTime

@Composable
fun SearchContactScreen(
    navigateBack: () -> Unit,
    onUserSelected: (String, Boolean) -> Unit,
    usersViewModel: UsersViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    val recentSearches = listOf(
        "barry.mccoquiner@example.com",
        "john.smith@example.com",
        "alice.johnson@example.com"
    )

    val allUsers by usersViewModel.users.collectAsState()

    val conversationsList by remember {
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

    val filteredUsers = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            allUsers.filter {
                it.email.contains(searchQuery, ignoreCase = true) ||
                        it.fullName.contains(searchQuery, ignoreCase = true)
            }
        } else emptyList()
    }

    Scaffold(
        modifier = Modifier.padding(Spacing.Inline),
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.search_for_user),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = navigateBack,
                actions = false,
                isSearch = true,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Sides),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(Spacing.Large))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.Inline)) {
                if (searchQuery.isBlank()) {
                    items(recentSearches) { email ->
                        val conversation = conversationsList.find { it.recipient.email == email }
                        if (conversation != null) {
                            Log.d("xd", conversation.id)
                            RecentSearchItem(
                                email = email,
                                onClick = { onUserSelected(conversation.id, true) }
                            )
                        }
                    }
                } else {
                    items(filteredUsers) { user ->
                        val conversation = conversationsList.find { it.recipient.email == user.email }
                        SearchUserItem(
                            fullName = user.fullName,
                            email = user.email,
                            onClick = {
                                onUserSelected(conversation?.id ?: user.id, conversation != null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchItem(
    email: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Spacing.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = stringResource(id = R.string.recent_search_icon)
        )
    }
}
