package com.mapify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mapbox.geojson.Point
import com.mapify.R
import com.mapify.ui.components.Map
import com.mapify.ui.components.SimpleTopBar

@Composable
fun ReportLocationScreen(
    navigateBack: (Double?, Double?) -> Unit
) {

    var clickedPoint by remember { mutableStateOf<Point?>(null) }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.report_location),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    val latitude = clickedPoint?.latitude()
                    val longitude = clickedPoint?.longitude()
                    navigateBack(latitude, longitude)
                },
                actions = false
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Map(
                navigateToDetail = {  },
                isClickable = true,
                onMapClickListener = { point ->
                    clickedPoint = point
                    true
                },
                clickedPoint = clickedPoint
            )
        }
    }
}