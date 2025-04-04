package com.mapify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapify.R
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateReportFloatingButton

@Composable
fun HomeScreen(
    navigateToProfile: () -> Unit, navigateToCreateReport: () -> Unit
) {
    //TODO: add logout icon (convenient for tests, anyway)

    Scaffold(topBar = {
        TopNavigationBar(
            onClickTop = {
                navigateToProfile()
            })
    }, bottomBar = { BottomNavigationBar(homeSelected = true) }, floatingActionButton = {
        CreateReportFloatingButton(navigateToCreateReport = navigateToCreateReport)
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(text = "Content", modifier = Modifier.padding(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    onClickTop: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.padding(horizontal = Spacing.Small),
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickTop) {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = stringResource(id = R.string.name_icon_description)
                )
            }
        },
    )
}
