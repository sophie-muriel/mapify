package com.mapify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mapify.R
import com.mapify.ui.theme.Spacing

@Composable
fun NotificationItem(
    title: String,
    status: String,
    supportingText: String,
    statusMessage: String,
    imageUrl: String? = null,
    statusColor: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            NotificationImage(imageUrl = imageUrl)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.Small * 3)
            ) {
                NotificationTextContent(
                    title = title,
                    supportingText = supportingText,
                    status = status,
                    statusMessage = statusMessage,
                    statusColor = statusColor
                )
            }
        }
    }
}

@Composable
private fun NotificationImage(imageUrl: String?) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .size(80.dp)
    ) {
        imageUrl?.let {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = it,
                contentDescription = stringResource(id = R.string.report_image),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun NotificationTextContent(
    title: String,
    supportingText: String,
    status: String,
    statusMessage: String,
    statusColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(status)
                }
                append(" \u2022 ")
                append(statusMessage)
            },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
