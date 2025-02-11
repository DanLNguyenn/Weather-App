package wpics.weather.models

import java.net.URL

/**
 * This data class keeps track of conditions for the current location.
 *
 * @version 1.0
 */
data class Condition(
    val text: String,
    val icon: URL,
    val code: Int
)