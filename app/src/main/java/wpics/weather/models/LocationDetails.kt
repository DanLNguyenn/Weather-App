package wpics.weather.models

import java.time.LocalDate
import java.time.LocalTime

/**
 * This data class keeps track of daily detailed information about the current location.
 *
 * @version 1.0
 */
data class LocationDetails(
    var sunrise: LocalTime,
    var sunset: LocalTime,
    var lastUpdate: LocalDate
)