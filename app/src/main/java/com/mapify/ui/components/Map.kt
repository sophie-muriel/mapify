package com.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapify.R

@Composable
fun Map(
    navigateToDetail: (String) -> Unit
){
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
            pitch(45.0)
        }
    }

    val markerResourceId by remember {
        mutableStateOf(R.drawable.red_marker)
    }

    val marker = rememberIconImage(
        key = markerResourceId,
        painter = painterResource(markerResourceId)
    )

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        mapState = rememberMapState {
            gesturesSettings = GesturesSettings { pitchEnabled = true }
        }
    ){
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }

            mapViewportState.transitionToFollowPuckState()
        }

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