package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapify.ui.components.MessageItem
import com.mapify.ui.theme.Spacing

@Composable
fun MessagesTab(){
    val dummyMessages = List(5) { "Lorem ipsum dolor sit amet, consectetur." }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Sides),
        verticalArrangement = Arrangement.spacedBy(Spacing.Inline),
    ) {
        items(dummyMessages) { message ->
            MessageItem(
                sender = "List item",
                message = message
            )
        }
    }
}