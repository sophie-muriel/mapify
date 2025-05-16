package com.mapify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapify.R
import com.mapify.model.Report
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

@Composable
fun Map(
    navigateToDetail: (String) -> Unit,
    isOneReport: Boolean = false,
    isReadOnly: Boolean = false,
    latitude: Double? = null,
    longitude: Double? = null,
    onMapClickListener: OnMapClickListener? = null,
    clickedPoint: Point? = null,
    reports: List<Report>? = null,
    isCenteredOnUser: Boolean = false,
    hasPrimaryFab: Boolean = true
){
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(Point.fromLngLat(-75.666375, 4.539860))
            pitch(45.0)
        }
    }

    val showFab = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200L)
        showFab.value = true
    }

    val markerResourceId by remember {
        mutableStateOf(R.drawable.red_marker)
    }

    val marker = rememberIconImage(
        key = markerResourceId,
        painter = painterResource(markerResourceId)
    )

    Box(modifier = Modifier.fillMaxSize()){
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            mapState = rememberMapState {
                gesturesSettings = GesturesSettings { pitchEnabled = true }
            },
            onMapClickListener = onMapClickListener
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

            if (isOneReport) {
                if(isReadOnly){
                    val pointToShow = if (latitude != null && longitude != null) {
                        Point.fromLngLat(longitude, latitude)
                    } else {
                        null
                    }
                    pointToShow?.let { point ->
                        PointAnnotation(point = point) {
                            iconImage = marker
                        }
                        mapViewportState.setCameraOptions {
                            center(point)
                            zoom(16.4)
                            pitch(45.0)
                        }
                    }
                }else{
                    val pointToShow = clickedPoint ?: if (latitude != null && longitude != null) {
                        Point.fromLngLat(longitude, latitude)
                    } else {
                        null
                    }
                    pointToShow?.let { point ->
                        PointAnnotation(point = point) {
                            iconImage = marker
                        }
                        if(clickedPoint != null){
                            mapViewportState.easeTo(
                                cameraOptions = CameraOptions.Builder()
                                    .center(point)
                                    .zoom(16.4)
                                    .pitch(45.0)
                                    .build(),
                                animationOptions = MapAnimationOptions.mapAnimationOptions {
                                    duration(500L)
                                }
                            )
                        }else{
                            mapViewportState.setCameraOptions {
                                center(point)
                                zoom(16.4)
                                pitch(45.0)
                            }
                        }
                    }
                }
            }else{
                reports?.forEach{ report ->
                    if(!report.isDeleted){
                        PointAnnotation(
                            point = Point.fromLngLat(report.location!!.longitude, report.location!!.latitude)
                        ){
                            iconImage = marker
                            interactionsState.onClicked {
                                navigateToDetail(report.id)
                                true
                            }
                        }
                    }
                }
            }
        }
        if (isCenteredOnUser && showFab.value) {
            Box(
                modifier = if(hasPrimaryFab) Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp)
                else Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                CreateFAB(
                    onClick = { mapViewportState.transitionToFollowPuckState() },
                    icon = Icons.Filled.MyLocation,
                    iconDescription = "Centrar en ubicación",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}