package com.mapify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    contentAlignment: Alignment,
    text: String = "",
    navIconVector: ImageVector,
    navIconDescription: String,
    onClickNavIcon: () -> Unit,
    actions: Boolean,
    firstActionIconVector: ImageVector? = null,
    firstActionIconDescription: String = "",
    firstOnClickAction: () -> Unit = {},
    secondAction: Boolean = false,
    secondActionIconVector: ImageVector? = null,
    secondActionIconDescription: String = "",
    secondOnClickAction: () -> Unit = {},

) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = contentAlignment) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onClickNavIcon) {
                Icon(
                    imageVector = navIconVector,
                    contentDescription = navIconDescription
                )
            }
        },
        actions = {
            if (actions && firstActionIconVector != null) {
                IconButton(onClick = firstOnClickAction) {
                    Icon(
                        imageVector = firstActionIconVector,
                        contentDescription = firstActionIconDescription,
                    )
                }
                if (secondAction && secondActionIconVector != null) {
                    IconButton(onClick = secondOnClickAction) {
                        Icon(
                            imageVector = secondActionIconVector,
                            contentDescription = secondActionIconDescription,
                        )
                    }
                }
            } else {
                IconButton(onClick = {}, enabled = false) {
                    Box(modifier = Modifier) {}
                }
            }
        }
    )
}