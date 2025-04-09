package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.mapify.R
import com.mapify.ui.components.SimpleTopBar

@Composable
fun ReportLocationScreen(
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SimpleTopBar(
                Alignment.CenterStart,
                stringResource(id = R.string.report_location),
                Icons.AutoMirrored.Filled.ArrowBack,
                stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
                false
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = "https://i.redd.it/tol9eauarmm61.jpg",
                    contentDescription = "Static map",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}