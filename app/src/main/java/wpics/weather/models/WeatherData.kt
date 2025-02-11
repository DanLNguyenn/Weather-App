package wpics.weather.models

data class WeatherData(
    val temperature: Double,    // Temperature (Celsius or Fahrenheit based on unit preference)
    val condition: String,      // Weather condition description (e.g., "Sunny", "Rain")
    val iconUrl: String,        // URL for the weather condition icon
    val humidity: Double,       // Humidity percentage
    val pressure: Double,       // Pressure (mb or inches based on unit)
    val precipitation: Double,  // Precipitation amount (mm or inches)
    val windSpeed: Double       // Wind speed (kph or mph based on unit)
)
