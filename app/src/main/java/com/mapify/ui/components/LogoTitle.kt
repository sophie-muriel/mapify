package com.mapify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.mapify.R

@Composable
fun LogoTitle(ratio: Float) {
    val isDarkTheme = isSystemInDarkTheme()
    val logo = if (isDarkTheme) R.drawable.mapify_dark else R.drawable.mapify_light

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.wrapContentHeight()
    ) {
        Image(
            painter = painterResource(id = logo),
            contentDescription = stringResource(id = R.string.icon_description),
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(ratio)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}