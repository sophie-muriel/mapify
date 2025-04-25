package com.mapify.ui.users.tabs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapify.R

@Composable
fun HomeTab(
    isAdmin: Boolean,
    navigateToDetail: (String) -> Unit
) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
        }
    }

    val markerResourceId by remember {
        mutableStateOf(R.drawable.red_marker)
    }

    val marker = rememberIconImage(key = markerResourceId, painter = painterResource(markerResourceId))

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
    ){
        PointAnnotation(
            point = Point.fromLngLat(-75.6491181, 4.4687891)
        ){
            iconImage = marker
            interactionsState.onClicked {
                navigateToDetail("1")
                true
            }
        }
        PointAnnotation(
            point = Point.fromLngLat(-75.575741, 4.600110)
        ){
            iconImage = marker
        }
    }
}