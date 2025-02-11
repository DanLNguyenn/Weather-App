package wpics.weather.models

/**
 * This data class keeps track of current wind for the day.
 *
 * @version 1.0
 */
data class Wind(
    var speedMPH: Double,
    var speedKPH: Double,
    var degree: Double,
    var direction: String
)