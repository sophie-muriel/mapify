package com.mapify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mapify.ui.theme.Spacing

@Composable
fun GenericDialog(
    title: String,
    message: String,
    onClose: (() -> Unit)? = null,
    onExit: () -> Unit,
    onCloseText: String = "",
    onExitText: String,
    textField: (@Composable () -> Unit)? = null
) {
    Dialog(onDismissRequest = { onClose?.invoke() ?: onExit() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Spacer(modifier = Modifier.height(Spacing.Sides))

            DialogTexts(title = title, message = message)

            textField?.invoke()

            DialogActions(
                onClose = onClose,
                onExit = onExit,
                onCloseText = onCloseText,
                onExitText = onExitText
            )

            Spacer(modifier = Modifier.height(Spacing.Sides))
        }
    }
}

@Composable
private fun DialogTexts(title: String, message: String) {
    Text(
        text = title,
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(horizontal = Spacing.Sides, vertical = Spacing.Small)
    )
    Text(
        text = message,
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .padding(horizontal = Spacing.Sides, vertical = Spacing.Small)
    )
}

@Composable
private fun DialogActions(
    onClose: (() -> Unit)?,
    onExit: () -> Unit,
    onCloseText: String,
    onExitText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Sides),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        onClose?.let {
            TextButton(onClick = it) {
                Text(
                    text = onCloseText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = onExit,
            modifier = Modifier
                .wrapContentSize()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = onExitText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
