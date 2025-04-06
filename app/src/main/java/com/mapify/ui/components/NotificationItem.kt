package com.mapify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.mapify.R

@Composable
fun NotificationItem(
    title: String,
    status: String,
    supportingText: String,
    statusColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            androidx.compose.material3.Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = stringResource(id = R.string.name_icon_description),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row {
                    Text(
                        text = status,
                        color = statusColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = " • $supportingText",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            thickness = 0.5.dp
        )
    }
}
