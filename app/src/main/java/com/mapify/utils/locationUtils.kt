import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.LocationServices
import com.mapify.R
import com.mapify.model.Location
import com.mapify.model.Report
import com.mapify.ui.components.GenericDialog
import com.mapify.ui.navigation.RouteScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
suspend fun getLocationName(
    context: Context,
    latitude: Double,
    longitude: Double
): Pair<String?, String?> = suspendCancellableCoroutine { cont ->

    val geocoder = Geocoder(context, Locale.getDefault())

    geocoder.getFromLocation(latitude, longitude, 1, object : GeocodeListener {
        override fun onGeocode(addresses: MutableList<android.location.Address>) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea
                val country = address.countryName
                cont.resume(Pair(city, country))
            } else {
                cont.resume(Pair(null, null))
            }
        }

        override fun onError(errorMessage: String?) {
            cont.resume(Pair(null, null))
        }
    })
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
suspend fun Location.updateCityCountry(context: Context) {
    withContext(Dispatchers.IO) {
        val locationName = getLocationName(context, latitude, longitude)
        city = locationName.first ?: "Unknown City"
        country = locationName.second ?: "Unknown Country"
    }
}


@Composable
fun HandleLocationPermission(
    onPermissionGranted: @Composable () -> Unit,
    permissionRationaleTitle: String = "Location permission required",
    permissionRationaleMessage: String = "This app requires location permission to function. Please enable it in settings."
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permission = Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var askedPermissionOnce by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        askedPermissionOnce = true
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
                hasPermission = granted
                if (!granted && !askedPermissionOnce) {
                    permissionLauncher.launch(permission)
                } else if (!granted) {
                    showPermissionDialog = true
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (hasPermission) {
        onPermissionGranted()
    } else {
        onPermissionGranted.invoke()
    }

    if (showPermissionDialog) {
        GenericDialog(
            title = permissionRationaleTitle,
            message = permissionRationaleMessage,
            onExitText = "Go to settings",
            onExit = {
                showPermissionDialog = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            onCloseText = "Cancel",
            onClose = {
                showPermissionDialog = false
                permissionLauncher.launch(permission)
            }
        )
    }
}

@Composable
fun LocationPermissionWrapper(
    currentRoute: String?,
    content: @Composable () -> Unit
) {
    val excludedRoutes = listOf(
        RouteScreen.Login::class.qualifiedName
    )
    if (currentRoute in excludedRoutes || currentRoute == null) {
        content()
    } else {
        HandleLocationPermission(onPermissionGranted = content)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
suspend fun fetchUserLocation(context: Context): com.mapify.model.Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return try {
        val androidLocation = fusedLocationClient.lastLocation.await()
        if (androidLocation != null) {
            val loc = com.mapify.model.Location(
                latitude = androidLocation.latitude,
                longitude = androidLocation.longitude
            )
            loc.updateCityCountry(context)
            loc
        } else null
    } catch (e: Exception) {
        null
    }
}

fun calculateDistanceMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val deltaLat = lat2 - lat1
    val deltaLon = lon2 - lon1
    val metersPerDegree = 111_320.0
    return metersPerDegree * sqrt(deltaLat * deltaLat + deltaLon * deltaLon)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun DistanceCalculator(
    context: Context,
    report: Report?,
    distance: MutableState<Double>
) {
    val permission = Manifest.permission.ACCESS_FINE_LOCATION

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionDenied = stringResource(id = R.string.location_access_permission_denied)
    val distanceCalculator = stringResource(id = R.string.distance_calculator)
    val waiting = stringResource(id = R.string.waiting_for_report_to_load)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, permissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(hasPermission, report?.location) {
        if (hasPermission && report != null && report.location != null) {
            val userLocation = fetchUserLocation(context)
            val reportLocation = report.location

            if (userLocation != null) {
                if (reportLocation != null) {
                    distance.value = calculateDistanceMeters(
                        lat1 = userLocation.latitude,
                        lon1 = userLocation.longitude,
                        lat2 = reportLocation.latitude,
                        lon2 = reportLocation.longitude
                    ) / 1000.0
                }
            } else {
                distance.value = 0.0
            }
        } else if (hasPermission && report == null) {
            Log.d(distanceCalculator, waiting)
            distance.value = 0.0
        } else if (!hasPermission) {
            Log.d(distanceCalculator, permissionDenied)
            distance.value = 0.0
        }
    }

    if (!hasPermission) {
        permissionLauncher.launch(permission)
    }
}
