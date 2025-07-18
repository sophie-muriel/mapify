package com.mapify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(
    contentAlignment: Alignment = Alignment.Center,
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
    tint: Color = LocalContentColor.current,
    isSearch: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    secondActionContent: (@Composable () -> Unit)? = null,
    areFiltersActive: Boolean = false
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = contentAlignment
            ) {
                if (isSearch) {
                    SearchBar(text, searchQuery, onSearchQueryChange)
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = if (secondAction && secondActionIconVector != null && contentAlignment != Alignment.CenterStart)
                            Modifier.offset(x = 24.dp) else Modifier
                    )
                }
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
            if (!isSearch) {
                if (actions && firstActionIconVector != null) {
                    IconButton(onClick = firstOnClickAction) {
                        Icon(
                            imageVector = firstActionIconVector,
                            contentDescription = firstActionIconDescription,
                            tint = if (areFiltersActive) MaterialTheme.colorScheme.primary else tint
                        )
                    }
                }
                secondActionContent?.invoke()
                if (secondAction && secondActionIconVector != null && secondActionContent == null) {
                    IconButton(onClick = secondOnClickAction) {
                        Icon(
                            imageVector = secondActionIconVector,
                            contentDescription = secondActionIconDescription
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SearchBar(
    placeholderText: String,
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = Spacing.Sides),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_icon)
            )
        },
        shape = MaterialTheme.shapes.large,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}
