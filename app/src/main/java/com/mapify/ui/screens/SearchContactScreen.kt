package com.mapify.ui.screens

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
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.SearchUserItem
import com.mapify.ui.components.SimpleTopBar

@Composable
fun SearchContactScreen(
    navigateBack: () -> Unit,
    onUserSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val recentSearches = listOf("Laura Mejía", "Carlos Ruiz", "Andrea Torres")
    val allUsers = listOf("Laura Mejía", "Carlos Ruiz", "Andrea Torres", "Ana López", "Luis Gómez")

    val filteredUsers = remember(searchQuery) {
        if (searchQuery.isNotBlank()) {
            allUsers.filter {
                it.contains(searchQuery, ignoreCase = true)
            }
        } else {
            emptyList()
        }
    }

    Scaffold(
        modifier = Modifier.padding(Spacing.Inline),
        topBar = {
            SimpleTopBar(
                Alignment.CenterStart,
                stringResource(id = R.string.search_for_user),
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
                false,
                isSearch = true,
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                }
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.Inline)
            ) {
                if (searchQuery.isBlank()) {
                    items(recentSearches) { username ->
                        RecentSearchItem(
                            username = username,
                            onClick = { onUserSelected(username) }
                        )
                    }
                } else {
                    items(filteredUsers) { username ->
                        SearchUserItem(
                            fullName = username,
                            usernameTag = "@${username.lowercase().replace(" ", "")}",
                            onClick = { onUserSelected(username) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchItem(
    username: String,
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
            text = username,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = "Search recent"
        )
    }
}
