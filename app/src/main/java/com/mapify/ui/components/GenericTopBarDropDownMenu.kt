package com.mapify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

sealed class MenuAction(
    val label: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
) {
    class Simple(
        label: String,
        icon: @Composable () -> Unit,
        onClick: () -> Unit
    ) : MenuAction(label, icon, onClick)
}

@Composable
fun MinimalDropdownMenu(
    menuItems: List<MenuAction>
) {
    val expanded = remember { mutableStateOf(false) }

    IconButton(onClick = { expanded.value = !expanded.value }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        menuItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.label) },
                onClick = {
                    expanded.value = false
                    item.onClick()
                },
                leadingIcon = item.icon
            )
        }
    }
}