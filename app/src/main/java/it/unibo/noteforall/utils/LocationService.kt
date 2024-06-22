package it.unibo.noteforall.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

enum class MonitoringStatus { Monitoring, NotMonitoring }

enum class StartMonitoringResult { Started, GPSDisabled, PermissionDenied }

data class Coordinates(val latitude: Double, val longitude: Double)

class LocationService (private val ctx: Context) {
    var coordinates: Coordinates? by mutableStateOf(null)
        private set
    private var monitoringStatus by mutableStateOf(MonitoringStatus.NotMonitoring)

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            with(p0.locations.last()) {
                coordinates = Coordinates(latitude, longitude)
            }
            endLocationRequest()
        }
    }

    fun requestCurrentLocation(): StartMonitoringResult {
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isLocationEnabled) return StartMonitoringResult.GPSDisabled

        val permissionGranted = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) return StartMonitoringResult.PermissionDenied

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        monitoringStatus = MonitoringStatus.Monitoring
        return StartMonitoringResult.Started
    }

    fun endLocationRequest() {
        if (monitoringStatus == MonitoringStatus.NotMonitoring) return
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        monitoringStatus = MonitoringStatus.NotMonitoring
    }
}
