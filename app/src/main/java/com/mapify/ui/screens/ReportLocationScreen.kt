package com.mapify.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.mapbox.geojson.Point
import com.mapify.R
import com.mapify.ui.components.Map
import com.mapify.ui.components.SimpleTopBar

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ReportLocationScreen(
    latitude: Double?,
    longitude: Double?,
    backIcon: Boolean,
    navigateBack: (Double?, Double?) -> Unit,
    isReadOnly: Boolean,
    isCenteredOnUser: Boolean,
    hasPrimaryFab: Boolean
) {
    val icon = if (backIcon) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.Check
    var clickedPoint by remember(latitude, longitude) {
        mutableStateOf(
            if (latitude != null && longitude != null) {
                Point.fromLngLat(longitude, latitude)
            } else {
                null
            }
        )
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                contentAlignment = Alignment.CenterStart,
                text = stringResource(id = R.string.report_location),
                navIconVector = icon,
                navIconDescription = stringResource(id = R.string.back_arrow_icon),
                onClickNavIcon = {
                    val capturedLatitude = clickedPoint?.latitude()
                    val capturedLongitude = clickedPoint?.longitude()
                    navigateBack(capturedLatitude, capturedLongitude)
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
                navigateToDetail = { },
                isOneReport = true,
                isReadOnly = isReadOnly,
                isCenteredOnUser = isCenteredOnUser,
                hasPrimaryFab = hasPrimaryFab,
                latitude = latitude,
                longitude = longitude,
                onMapClickListener = { point ->
                    clickedPoint = point
                    true
                },
                clickedPoint = clickedPoint
            )
        }
    }
}