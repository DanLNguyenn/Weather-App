package wpics.weather.models

/**
 * This data class keeps track of daily details related to the current location.
 *
 * @version 1.0
 */
data class Location(
    val latitude: Double,       // Latitude of the location
    val longitude: Double,      // Longitude of the location
    val locality: String,       // City or locality name
    val adminArea: String,      // Administrative area (e.g., state, province)
    val country: String,        // Country name
    var details: LocationDetails? = null  // Optional details (e.g., sunrise, sunset times)
)