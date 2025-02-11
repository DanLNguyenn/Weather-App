package wpics.weather.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.VolleyError
import org.json.JSONObject

/**
 * This data repository class stores the weather data
 *
 * @version 1.0
 */
class WeatherDataRepository private constructor(context: Context) {

    // Class attributes
    private val weatherDataFetcher = WeatherDataFetcher(context.applicationContext)
    var astronomyResult = MutableLiveData<JSONObject>()
    var weatherResult = MutableLiveData<JSONObject>()

    private val fetchListener = object : WeatherDataFetcher.OnWeatherDataReceivedListener {

        /**
         * This executes when we receive astronomy data.
         *
         * @param result The result as a [JSONObject]
         */
        override fun onAstronomyDataReceived(result: JSONObject) {
            astronomyResult.value = result
        }

        /**
         * This executes when we receive weather data.
         *
         * @param result The result as a [JSONObject]
         */
        override fun onWeatherDataReceived(result: JSONObject) {
            weatherResult.value = result
        }

        /**
         * This executes when we encounter some kind of error
         *
         * @param error A [VolleyError]
         */
        override fun onErrorResponse(error: VolleyError) {
            error.printStackTrace()
        }
    }

    /**
     * This applies the singleton pattern so we get exactly one [WeatherDataRepository].
     */
    companion object {
        private var instance: WeatherDataRepository? = null

        fun getInstance(context: Context): WeatherDataRepository {
            if (instance == null) {
                instance = WeatherDataRepository(context)
            }
            return instance!!
        }
    }

    /**
     * This function retrieves the astronomy data from the weather API
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     */
    fun fetchAstronomyResult(baseURL: String, key: String, location: String) =
        weatherDataFetcher.getAstronomyInformation(baseURL, key, location, fetchListener)

    /**
     * This function retrieves the current temperature and forecast from the weather API
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     */
    fun fetchWeatherResult(baseURL: String, key: String, location: String) =
        weatherDataFetcher.getWeatherInformation(baseURL, key, location, fetchListener)
}