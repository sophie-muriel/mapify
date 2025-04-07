package com.mapify.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CreateFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    iconDescription: String
) {

    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
        )
    }

}