package wpics.weather.models

/**
 * This data class keeps track of current precipitation for the day.
 *
 * @version 1.0
 */
data class Precipitation(
    var precipitationMM: Double,
    var precipitationIN: Double
)