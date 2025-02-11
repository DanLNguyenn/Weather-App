package wpics.weather.models

/**
 * This data class keeps track of current, average, minimum and maximum temperature for the day.
 *
 * @version 1.0
 */
data class Temperature(
    var temperatureC: Double,
    var temperatureF: Double,
    var avgTemperatureC: Double,
    var avgTemperatureF: Double,
    var minTemperatureC: Double,
    var maxTemperatureC: Double,
    var minTemperatureF: Double,
    var maxTemperatureF: Double
)