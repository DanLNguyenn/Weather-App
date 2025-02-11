package wpics.weather

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import wpics.weather.controllers.ErrorActivity
import wpics.weather.fragments.WeatherFragment

/**
 * This contains the application logic for requesting location permission and invoking
 * [WeatherFragment] to render weather data.
 *
 * @version 1.0
 */
class MainActivity : AppCompatActivity() {

    // Class attributes: WeatherAPI
    private val weatherAPIKey = BuildConfig.WEATHER_API_KEY

    // Class attributes: location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    // Class attributes: WeatherFragment
    private val weatherFragment = WeatherFragment()

    /**
     * This is the first method that gets called when the activity starts
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the WeatherAPI key
        if (weatherAPIKey.isNotEmpty()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            if (hasLocationPermission()) {
                getLastLocation()
            }
        } else {
            apiError("Error accessing the WeatherAPI key. Please check that you have correctly supplied the correct key.")
        }
    }

    /**
     * This helper function uses the provided error message to start [ErrorActivity].
     *
     * @param msg The error message to be displayed
     */
    private fun apiError(msg: String) {
        // Store the error message
        val b = Bundle()
        b.putString(
            "errorMsg",
            msg
        )

        val intent = Intent(this, ErrorActivity::class.java)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

    // ===========================================================
    // Location-Related Functions
    // ===========================================================

    /**
     * This helper function retrieves a new location
     */
    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setWaitForAccurateLocation(false).setMaxUpdates(1).build()

        // Callback function for when a new location has been updated
        val locationCallback = object : LocationCallback() {

            /**
             * This function runs when we obtain a location result.
             *
             * @param locationResult A new [LocationResult]
             */
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    // Store the location's latitude and longitude
                    val args = Bundle()
                    args.putDouble("latitude", it.latitude)
                    args.putDouble("longitude", it.longitude)
                    args.putString("weatherAPIKey", weatherAPIKey)
                    weatherFragment.arguments = args

                    supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                        .add(R.id.idWeatherFragment, weatherFragment, null).commit()
                }
            }
        }

        // Wait until we get an location update
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    /**
     * This helper function retrieves the last location
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                // Case 1: No last location, so we get a new location
                if (location == null) {
                    getNewLocation()
                }
                // Case 2: Use the last location to get the weather information
                else {
                    // Store the location's latitude and longitude
                    val args = Bundle()
                    args.putDouble("latitude", location.latitude)
                    args.putDouble("longitude", location.longitude)
                    args.putString("weatherAPIKey", weatherAPIKey)
                    weatherFragment.arguments = args

                    supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                        .add(R.id.idWeatherFragment, weatherFragment, null).commit()
                }
            }
    }

    /**
     * This helper function checks to see if we have granted location permission
     *
     * @return True if we have granted permission, false otherwise.
     */
    private fun hasLocationPermission(): Boolean {
        // Request fine location permission if not already granted
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return false
        }

        return true
    }

    /**
     * This requests the user to allow accessing the location services.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocation()
        }
    }
}