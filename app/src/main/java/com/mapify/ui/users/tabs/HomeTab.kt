package com.mapify.ui.users.tabs

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapify.R
import com.mapify.ui.components.GenericDialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.runtime.DisposableEffect

@Composable
fun HomeTab(
    isAdmin: Boolean,
    navigateToDetail: (String) -> Unit
) {

    val context = LocalContext.current
    val permission = Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var askedPermissionOnce by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        askedPermissionOnce = true
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val checkAndRequestLocationPermission = {
            val permissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

            hasPermission = permissionGranted

            if (!hasPermission && !askedPermissionOnce) {
                permissionLauncher.launch(permission)
            } else if (!hasPermission) {
                showPermissionDialog = true
            }
        }

        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkAndRequestLocationPermission()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        checkAndRequestLocationPermission()

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if(showPermissionDialog){
        GenericDialog(
            title = "Location permission required",
            message = "This app requires location permission to properly function. If you want to continue, you must manually enable location permission in your system settings.",
            onExit = {
                showPermissionDialog = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            onExitText = "Go to settings",
            onClose = {
                showPermissionDialog = false
                permissionLauncher.launch(permission)
            },
            onCloseText = "Cancel"
        )
    }


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

    if(hasPermission){
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
    }else if(askedPermissionOnce){
        Text(
            text = "Disabled map due to no location permission"
        )
    }
}