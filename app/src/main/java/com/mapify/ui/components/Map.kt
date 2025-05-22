package com.mapify.ui.components

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.mapify.model.Location
import fetchUserLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    val context = LocalContext.current

    var userLocationLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var userLocationLatitude by rememberSaveable { mutableStateOf<Double?>(null) }

    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

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
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch @androidx.annotation.RequiresPermission(
                            allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]
                        ) {
                            val fetchedLocation = fetchUserLocation(context)
                            userLocationLongitude = fetchedLocation?.longitude
                            userLocationLatitude = fetchedLocation?.latitude
                            val point = userLocationLongitude?.let { lng ->
                                userLocationLatitude?.let { lat ->
                                Point.fromLngLat(lng, lat)
                            } }
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
                        }
                    },
                    icon = Icons.Filled.MyLocation,
                    iconDescription = stringResource(id = R.string.center_on_user_location_icon),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}