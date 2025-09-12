package com.mapify.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.model.Participant
import com.mapify.model.User
import com.mapify.ui.components.SearchUserItem
import com.mapify.ui.components.SimpleTopBar
import com.mapify.ui.navigation.LocalMainViewModel
import com.mapify.ui.theme.Spacing
import com.mapify.utils.SharedPreferencesUtils
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SearchContactScreen(
    navigateBack: () -> Unit,
    onUserSelected: (String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val userId = SharedPreferencesUtils.getPreference(context)["userId"]
    if (userId == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "error user id missing",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(Spacing.Large)
            )
        }
        return
    }

    val usersViewModel = LocalMainViewModel.current.usersViewModel
    val conversationsViewModel = LocalMainViewModel.current.conversationsViewModel
    val allUsers by usersViewModel.users.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val conversations by conversationsViewModel.conversations.collectAsState()
    val recentSearches = conversationsViewModel.recentSearches
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        usersViewModel.loadUser(userId)
        conversationsViewModel.getVisibleConversationsForUser(userId)
    }

    val userConversations = conversations.filter { conv ->
        conv.participants.any { it.id == userId }
    }

    val filteredUsers = remember(searchQuery) {
        allUsers.filter {
            it.id != userId && (searchQuery.isBlank() ||
                    it.id.contains(searchQuery, ignoreCase = true) ||
                    it.fullName.contains(searchQuery, ignoreCase = true))
        }
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
                actions = true,
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
                    if (recentSearches.isEmpty()) {
                        item {
                            Text(
                                text = "No recent searches",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = Spacing.Large)
                            )
                        }
                    } else {
                        items(recentSearches, key = { it.id }) { u ->
                            RecentSearchItem(
                                user = u,
                                onClick = {
                                    val conversation = userConversations.firstOrNull { conv ->
                                        conv.participants.any { it.id == userId } &&
                                                conv.participants.any { it.id == u.id }
                                    }
                                    if (conversation != null) {
                                        conversationsViewModel.markAsRead(conversation.id, userId)
                                        onUserSelected(conversation.id, true)
                                    } else {
                                        scope.launch {
                                            val user = usersViewModel.user.value
                                            if (user == null) {
                                                Log.e("SearchContact", "Current user not found")
                                                return@launch
                                            }
                                            val newConversation = conversationsViewModel.createConversation(
                                                sender = Participant(userId, user.fullName),
                                                recipient = Participant(u.id, u.fullName)
                                            )
                                            conversationsViewModel.addRecentSearch(u)
                                            onUserSelected(newConversation.id, false)
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else {
                    if (filteredUsers.isEmpty()) {
                        item {
                            Text(
                                text = "User not found",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = Spacing.Large)
                            )
                        }
                    } else {
                        items(filteredUsers, key = { it.id }) { u ->
                            SearchUserItem(
                                fullName = u.fullName,
                                email = u.email,
                                onClick = {
                                    val conversation = userConversations.firstOrNull { conv ->
                                        conv.participants.any { it.id == userId } &&
                                                conv.participants.any { it.id == u.id }
                                    }
                                    if (conversation != null) {
                                        conversationsViewModel.markAsRead(conversation.id, userId)
                                        onUserSelected(conversation.id, true)
                                    } else {
                                        scope.launch {
                                            val user = usersViewModel.user.value
                                            if (user == null) {
                                                Log.e("SearchContact", "Current user not found")
                                                return@launch
                                            }
                                            val newConversation = conversationsViewModel.createConversation(
                                                sender = Participant(userId, user.fullName),
                                                recipient = Participant(u.id, u.fullName)
                                            )
                                            conversationsViewModel.addRecentSearch(u)
                                            onUserSelected(newConversation.id, false)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchItem(
    user: User,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Spacing.Small, horizontal = Spacing.Large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
        )
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = stringResource(id = R.string.recent_search_icon),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}