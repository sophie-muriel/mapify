package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapify.R
import com.mapify.ui.theme.Spacing
import com.mapify.ui.components.BottomNavigationBar
import com.mapify.ui.components.CreateReportFloatingButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
        navigateToCreateReport: () -> Unit
){

    Scaffold(
        topBar = {
            TopAppBar(modifier = Modifier.padding(horizontal = Spacing.Small), title = {
                Text(
                    text = "Explore Screen"
                )
            }, navigationIcon = {
                IconButton(
                    onClick = {
                        //navigateToCreateReport()
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_arrow_icon)
                    )
                }
            })
        }, bottomBar = {
            BottomNavigationBar(searchSelected = true)
        }, floatingActionButton = {
            CreateReportFloatingButton(
                navigateToCreateReport = {
                    navigateToCreateReport()
                }
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

        }
    }
}