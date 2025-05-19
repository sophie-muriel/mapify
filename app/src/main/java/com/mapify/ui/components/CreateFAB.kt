package com.mapify.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CreateFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    iconDescription: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {

    FloatingActionButton(
        onClick = onClick,
        containerColor = color
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconDescription,
        )
    }

}