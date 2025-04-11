package com.mapify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun MinimalDropdownMenu(isAdmin: Boolean? = null, isCreator: Boolean? = null) {
    val expanded = remember { mutableStateOf(false) }

    IconButton(onClick = { expanded.value = !expanded.value }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More options")
    }
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(if (isCreator != null) "Creator" else "Viewer")
            },
            onClick = { /* Do something... */ }
        )
        DropdownMenuItem(
            text = { Text("Option 2") },
            onClick = { /* Do something... */ }
        )
    }
}