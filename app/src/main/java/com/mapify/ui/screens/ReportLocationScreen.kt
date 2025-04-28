package com.mapify.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapify.R
import com.mapify.ui.components.SimpleTopBar

@Composable
fun ReportLocationScreen(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
            pitch(45.0)
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.report_location),
                navIconVector = Icons.AutoMirrored.Filled.ArrowBack,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = { navigateBack() },
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
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                onMapClickListener = { point ->
                    Toast.makeText(context, "Click en ${point.latitude()}, ${point.longitude()}", Toast.LENGTH_SHORT).show()
                    true
                }
            )
        }
    }
}