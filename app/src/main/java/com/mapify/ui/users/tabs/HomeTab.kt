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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mapify.ui.components.Map

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

    val lifecycleOwner = LocalLifecycleOwner.current

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

    if(hasPermission){
        Map(
            navigateToDetail = navigateToDetail
        )
    }else if(askedPermissionOnce){
        Text(
            text = "Disabled map due to no location permission"
        )
    }
}