package wpics.weather.models

data class ForecastData(
    val date: String,          // Forecast date in "YYYY-MM-DD" format
    val maxTemp: Double,       // Maximum temperature for the day
    val minTemp: Double,       // Minimum temperature for the day
    val condition: String,     // Weather condition for the day
    val iconUrl: String        // URL for the forecast condition icon
)
