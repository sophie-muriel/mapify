package com.mapify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mapify.ui.theme.Spacing

@Composable
fun GenericDialog(
    title: String,
    message: String,
    onClose: () -> Unit,
    onExit: () -> Unit,
    onCloseText: String,
    onExitText: String,
    textField: (@Composable () -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Spacer(modifier = Modifier.height(Spacing.Sides))

            Text(
                text = title,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = message,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(
                    horizontal = Spacing.Sides, vertical = Spacing.Small
                ),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(Spacing.Small))

            if (textField != null) {
                textField()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Spacing.Sides
                    ), horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onClose()
                    }) {
                    Text(
                        text = onCloseText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(40.dp),
                    onClick = {
                        onExit()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) {
                    Text(
                        text = onExitText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Sides))
        }
    }
}