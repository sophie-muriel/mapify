package com.mapify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapify.ui.theme.Spacing

@Composable
fun SearchUserItem(
    fullName: String,
    email: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Spacing.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileIcon(
            fallbackText = fullName,
            size = 40.dp
        )

        Spacer(modifier = Modifier.width(Spacing.Inline))

        Column {
            Text(
                text = fullName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
