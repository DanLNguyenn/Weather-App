package wpics.weather.data

import android.content.Context
import android.net.Uri
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * This class uses the [Volley] library to get the weather information
 *
 * @param context The context that called this class
 *
 * @version 1.0
 */
class WeatherDataFetcher(context: Context) {

    /**
     * Listener to store the data received from volley.
     */
    interface OnWeatherDataReceivedListener {

        /**
         * This executes when we receive astronomy data.
         *
         * @param result The result as a [JSONObject]
         */
        fun onAstronomyDataReceived(result: JSONObject)

        /**
         * This executes when we receive weather data.
         *
         * @param result The result as a [JSONObject]
         */
        fun onWeatherDataReceived(result: JSONObject)

        /**
         * This executes when we encounter some kind of error
         *
         * @param error A [VolleyError]
         */
        fun onErrorResponse(error: VolleyError)
    }

    // Class attributes
    private var requestQueue = Volley.newRequestQueue(context)
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * This retrieves the current astronomy data using the specified API url
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     * @param listener The listener where we will be storing the results from the call
     *
     * @return A JSON string containing the request results. If it is blank, it means that something went wrong.
     */
    fun getAstronomyInformation(
        baseURL: String,
        key: String,
        location: String,
        listener: OnWeatherDataReceivedListener
    ) {
        val url = Uri.parse(baseURL).buildUpon()
            .appendPath("astronomy.json")
            .appendQueryParameter("key", key)
            .appendQueryParameter("q", location)
            .appendQueryParameter("dt", LocalDate.now().format(dateFormat))
            .build().toString()

        // Request a JSON string response
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response -> listener.onAstronomyDataReceived(response) },
            { error -> listener.onErrorResponse(error) }
        )

        // Add it to our queue
        requestQueue.add(request)
    }

    /**
     * This retrieves the current weather and forecast using the specified API url
     *
     * @param baseURL The base API url
     * @param key The API key
     * @param location The current location
     * @param listener The listener where we will be storing the results from the call
     *
     * @return A JSON string containing the request results. If it is blank, it means that something went wrong.
     */
    fun getWeatherInformation(
        baseURL: String,
        key: String,
        location: String,
        listener: OnWeatherDataReceivedListener
    ) {
        val url = Uri.parse(baseURL).buildUpon()
            .appendPath("forecast.json")
            .appendQueryParameter("key", key)
            .appendQueryParameter("q", location)
            .appendQueryParameter("days", "7")
            .appendQueryParameter("aqi", "no")
            .appendQueryParameter("alerts", "yes")
            .build().toString()

        // Request a JSON string response
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response -> listener.onWeatherDataReceived(response) },
            { error -> listener.onErrorResponse(error) }
        )

        // Add it to our queue
        requestQueue.add(request)
    }
}