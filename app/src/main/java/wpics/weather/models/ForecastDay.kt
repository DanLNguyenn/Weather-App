package wpics.weather.models

/**
 * This data class keeps track of weather conditions for a forecast day.
 *
 * @version 1.0
 */
data class ForecastDay(
    var condition: Condition,
    var temperature: Temperature,
    var humidity: Humidity,
    var wind: Wind,
    var precipitation: Precipitation,
    var snow: Snow
)