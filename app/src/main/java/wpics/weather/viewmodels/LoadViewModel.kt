package wpics.weather.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import wpics.weather.data.WeatherDataRepository

/**
 * This view-model class uses the loads results from the data repository
 *
 * @version 1.0
 */
class LoadViewModel(application: Application) : AndroidViewModel(application) {

    // Class attributes
    private val weatherDataRepo = WeatherDataRepository.getInstance(application)
    var astronomyResult = weatherDataRepo.astronomyResult
    var weatherResult = weatherDataRepo.weatherResult

    /**
     * This function retrieves the astronomy data from the weather API
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     */
    fun fetchAstronomyData(baseURL: String, key: String, location: String) =
        weatherDataRepo.fetchAstronomyResult(baseURL, key, location)

    /**
     * This function retrieves the current temperature and forecast from the weather API
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     */
    fun fetchWeatherResult(baseURL: String, key: String, location: String) =
        weatherDataRepo.fetchWeatherResult(baseURL, key, location)
}