package wpics.weather.fragments

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import org.json.JSONObject
import wpics.weather.R
import wpics.weather.models.*
import wpics.weather.viewmodels.LoadViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeatherFragment : Fragment() {

    private lateinit var currentLoc: Location
    private lateinit var geocoder: Geocoder
    private lateinit var rootView: View

    private val loadViewModel: LoadViewModel by lazy {
        ViewModelProvider(this)[LoadViewModel::class.java]
    }

    private lateinit var unitPreference: String

    // Properties to store current weather and forecast data
    private var currentWeather: WeatherData? = null
    private var forecast: List<ForecastData> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false)

        if (container != null) {
            geocoder = Geocoder(container.context, Locale.getDefault())
        }

        // Load the unit preference
        unitPreference = getUnitPreference(requireContext())

        // Load the arguments
        val args = arguments
        if (args != null) {
            args.getString("weatherAPIKey")?.let {
                getWeatherInfo(
                    args.getDouble("latitude"), args.getDouble("longitude"),
                    it
                )
            }
        }

        // Handle unit toggle button click
        val toggleUnitButton: Button = rootView.findViewById(R.id.btn_toggle_unit)
        toggleUnitButton.setOnClickListener {
            toggleUnitPreference()
            refreshWeatherData()
        }

        return rootView
    }

    private fun toggleUnitPreference() {
        unitPreference = when (unitPreference) {
            "Imperial" -> "Metric"
            "Metric" -> "Imperial"
            else -> "Imperial"
        }
        setUnitPreference(requireContext(), unitPreference)
    }

    private fun getWeatherInfo(latitude: Double, longitude: Double, weatherAPIKey: String) {
        geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) { addresses ->
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                currentLoc = Location(
                    latitude,
                    longitude,
                    address.locality ?: "Unknown City",
                    address.adminArea ?: "Unknown State",
                    address.countryName ?: "Unknown Country"
                )

                // Set location text
                val locationTextView: TextView = rootView.findViewById(R.id.locationTextView)
                locationTextView.text = "${currentLoc.locality}, ${currentLoc.adminArea}, ${currentLoc.country}"

                // Fetch Astronomy Data
                loadViewModel.fetchAstronomyData(
                    "https://api.weatherapi.com/v1/",
                    weatherAPIKey,
                    "${currentLoc.latitude},${currentLoc.longitude}"
                )

                // Fetch Weather and Forecast Data
                loadViewModel.fetchWeatherResult(
                    "https://api.weatherapi.com/v1/",
                    weatherAPIKey,
                    "${currentLoc.latitude},${currentLoc.longitude}"
                )
            }
        }

        // Render data when it is available
        renderData()
    }

    private fun refreshWeatherData() {
        // Re-fetch the weather data with the new unit preference
        val args = arguments
        if (args != null) {
            args.getString("weatherAPIKey")?.let {
                getWeatherInfo(
                    args.getDouble("latitude"), args.getDouble("longitude"),
                    it
                )
            }
        }
    }

    private fun renderData() {
        loadViewModel.astronomyResult.observe(viewLifecycleOwner) {
            val jsonObject = loadViewModel.astronomyResult.value
            if (jsonObject != null) {
                val jsonAstronomyObject =
                    jsonObject.getJSONObject("astronomy").getJSONObject("astro")

                val sunrise = jsonAstronomyObject.getString("sunrise")
                    .replace("p", "PM").replace("a", "AM")
                val sunset = jsonAstronomyObject.getString("sunset")
                    .replace("p", "PM").replace("a", "AM")
                val localDate = jsonObject.getJSONObject("location").getString("localtime")
                currentLoc.details = LocationDetails(
                    LocalTime.parse(
                        sunrise,
                        DateTimeFormatter.ofPattern("hh:mm a")
                    ),
                    LocalTime.parse(sunset, DateTimeFormatter.ofPattern("hh:mm a")),
                    LocalDate.parse(localDate, DateTimeFormatter.ofPattern("yyyy-MM-dd H:m"))
                )

                renderAstronomyData()
            }
        }

        loadViewModel.weatherResult.observe(viewLifecycleOwner) {
            val jsonObject = loadViewModel.weatherResult.value
            if (jsonObject != null) {
                currentWeather = parseCurrentWeather(jsonObject.getJSONObject("current"))
                forecast = parseForecast(jsonObject.getJSONObject("forecast"))

                renderWeatherData(currentWeather!!)
                renderForecastData(forecast)
            }
        }
    }

    private fun renderAstronomyData() {
        val sunriseTextView: TextView = rootView.findViewById(R.id.tv_sunrise)
        val sunsetTextView: TextView = rootView.findViewById(R.id.tv_sunset)

        currentLoc.details?.let {
            sunriseTextView.text = "Sunrise: ${it.sunrise}"
            sunsetTextView.text = "Sunset: ${it.sunset}"
        }
    }

    private fun parseCurrentWeather(jsonCurrent: JSONObject): WeatherData {
        val condition = jsonCurrent.getJSONObject("condition")
        return when (unitPreference) {
            "Metric" -> WeatherData(
                temperature = jsonCurrent.getDouble("temp_c"),
                condition = condition.getString("text"),
                iconUrl = condition.getString("icon"),
                humidity = jsonCurrent.getDouble("humidity"),
                pressure = jsonCurrent.getDouble("pressure_mb"),
                precipitation = jsonCurrent.getDouble("precip_mm"),
                windSpeed = jsonCurrent.getDouble("wind_kph")
            )
            else -> WeatherData(
                temperature = jsonCurrent.getDouble("temp_f"),
                condition = condition.getString("text"),
                iconUrl = condition.getString("icon"),
                humidity = jsonCurrent.getDouble("humidity"),
                pressure = jsonCurrent.getDouble("pressure_in"),
                precipitation = jsonCurrent.getDouble("precip_in"),
                windSpeed = jsonCurrent.getDouble("wind_mph")
            )
        }
    }

    private fun parseForecast(jsonForecast: JSONObject): List<ForecastData> {
        val forecastList = mutableListOf<ForecastData>()
        val daysArray = jsonForecast.getJSONArray("forecastday")

        for (i in 0 until daysArray.length()) {
            val dayObject = daysArray.getJSONObject(i)
            val day = dayObject.getJSONObject("day")
            val condition = day.getJSONObject("condition")

            val forecastData = when (unitPreference) {
                "Metric" -> ForecastData(
                    date = dayObject.getString("date"),
                    maxTemp = day.getDouble("maxtemp_c"),
                    minTemp = day.getDouble("mintemp_c"),
                    condition = condition.getString("text"),
                    iconUrl = condition.getString("icon")
                )
                else -> ForecastData(
                    date = dayObject.getString("date"),
                    maxTemp = day.getDouble("maxtemp_f"),
                    minTemp = day.getDouble("mintemp_f"),
                    condition = condition.getString("text"),
                    iconUrl = condition.getString("icon")
                )
            }
            forecastList.add(forecastData)
        }
        return forecastList
    }

    private fun renderWeatherData(currentWeather: WeatherData) {
        val currentTempTextView: TextView = rootView.findViewById(R.id.tv_temperature)
        val conditionTextView: TextView = rootView.findViewById(R.id.currentConditionTextView)
        val weatherIconView: ImageView = rootView.findViewById(R.id.iv_weather_icon)

        currentTempTextView.text = "${currentWeather.temperature}°${if (unitPreference == "Metric") "C" else "F"}"
        conditionTextView.text = currentWeather.condition
        renderWeatherIcon(currentWeather.iconUrl, weatherIconView)

        // Change background color based on weather condition
        updateBackgroundColor(currentWeather.condition)
    }

    private fun renderForecastData(forecastList: List<ForecastData>) {
        val forecastContainer: LinearLayout = rootView.findViewById(R.id.forecastContainer)
        forecastContainer.removeAllViews()

        forecastList.forEach { forecastData ->
            val forecastItem = LinearLayout(context)
            forecastItem.orientation = LinearLayout.HORIZONTAL
            forecastItem.setPadding(8, 8, 8, 8)

            val dateTextView = TextView(context)
            dateTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            dateTextView.textSize = 16f
            dateTextView.text = forecastData.date

            val forecastTempTextView = TextView(context)
            forecastTempTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            forecastTempTextView.textSize = 16f
            forecastTempTextView.text = "${forecastData.maxTemp}°${if (unitPreference == "Metric") "C" else "F"} / ${forecastData.minTemp}°${if (unitPreference == "Metric") "C" else "F"}"

            val conditionImageView = ImageView(context)
            conditionImageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            conditionImageView.load("https:${forecastData.iconUrl}")

            forecastItem.addView(dateTextView)
            forecastItem.addView(forecastTempTextView)
            forecastItem.addView(conditionImageView)

            forecastContainer.addView(forecastItem)
        }
    }

    private fun renderWeatherIcon(url: String, imageView: ImageView) {
        imageView.load("https:$url")
    }

    // Method to update background color based on weather condition
    private fun updateBackgroundColor(condition: String) {
        val colorResId = getBackgroundColorForWeather(condition)
        rootView.setBackgroundColor(ContextCompat.getColor(requireContext(), colorResId))
    }

    // Helper method to map weather conditions to background colors
    private fun getBackgroundColorForWeather(condition: String): Int {
        return when (condition.toLowerCase(Locale.ROOT)) {
            "sunny" -> R.color.clear_day
            "clear night" -> R.color.clear_night
            "partly cloudy" -> R.color.partly_cloudy_day
            "cloudy" -> R.color.cloudy
            "overcast" -> R.color.cloudy
            "rain" -> R.color.rain
            "showers" -> R.color.rain
            "snow" -> R.color.snow
            "sleet" -> R.color.sleet
            "fog" -> R.color.fog
            "mist" -> R.color.fog
            "thunderstorm" -> R.color.rain
            "night" -> R.color.clear_night
            else -> R.color.light_background
        }
    }

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val UNIT_KEY = "unit_preference"

        fun setUnitPreference(context: Context, unit: String) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString(UNIT_KEY, unit)
            editor.apply()
        }

        fun getUnitPreference(context: Context): String {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(UNIT_KEY, "Imperial") ?: "Imperial"
        }
    }
}
