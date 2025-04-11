package com.mapify.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Unpublished
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.mapify.R

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
    isAdmin: Boolean? = null,
    isCreator: Boolean? = null,
    isChat: Boolean? = null
) {
    val expanded = remember { mutableStateOf(false) }

    IconButton(onClick = { expanded.value = !expanded.value }) {
        Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_options_icon))
    }

    val menuItems = getMenuItems(isAdmin, isCreator, isChat)

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        menuItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.label) },
                onClick = item.onClick,
                leadingIcon = item.icon
            )
        }
    }
}

@Composable
fun getMenuItems(isAdmin: Boolean?, isCreator: Boolean?, isChat: Boolean?): List<MenuAction> {
    return when {
        isChat == true -> listOf(
            MenuAction.Simple(stringResource(id = R.string.mark_as_read), { Icon(Icons.Default.MarkChatRead, null) }, {}),
            MenuAction.Simple(stringResource(id = R.string.mark_as_unread), { Icon(Icons.Default.MarkChatUnread, null) }, {}),
            MenuAction.Simple(stringResource(id = R.string.delete), {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }, {})
        )

        isCreator == true -> listOf(
            MenuAction.Simple(stringResource(id = R.string.edit), { Icon(Icons.Default.Edit, null) }, {}),
            MenuAction.Simple(stringResource(id = R.string.delete), {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }, {})
        )

        isAdmin == true -> listOf(
            MenuAction.Simple(stringResource(id = R.string.verify), { Icon(Icons.Default.CheckCircle, null) }, {}),
            MenuAction.Simple(stringResource(id = R.string.reject), { Icon(Icons.Default.Unpublished, null) }, {}),
            MenuAction.Simple(stringResource(id = R.string.delete), {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }, {})
        )

        else -> listOf(
            MenuAction.Simple(stringResource(id = R.string.boost_priority), {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, null)
            }, {})
        )
    }
}