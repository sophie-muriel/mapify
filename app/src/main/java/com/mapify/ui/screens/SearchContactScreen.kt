package com.mapify.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.SearchUserItem

@Composable
fun SearchContactScreen(
    navigateBack: () -> Unit,
    onUserSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val recentSearches = listOf("Laura Mejía", "Carlos Ruiz", "Andrea Torres")
    val allUsers = listOf("Laura Mejía", "Carlos Ruiz", "Andrea Torres", "Ana López", "Luis Gómez")

    val filteredUsers = remember(searchQuery) {
        if (searchQuery.text.isNotBlank()) {
            allUsers.filter {
                it.contains(searchQuery.text, ignoreCase = true)
            }
        } else {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.width(Spacing.Small))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar usuario") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.Large))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.Inline)
        ) {
            if (searchQuery.text.isBlank()) {
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
