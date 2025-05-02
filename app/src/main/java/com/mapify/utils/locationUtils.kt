import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Locale
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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