package wpics.weather.controllers

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import wpics.weather.R

/**
 * This is where we handle any error messages produced by the app.
 *
 * @version 1.0
 */
class ErrorActivity : AppCompatActivity() {

    /**
     * This is the first method that gets called when the activity starts
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        // Grab the extra information
        val b = intent.extras
        if (b != null) {
            findViewById<TextView>(R.id.idErrorMsg).text = b.getString("errorMsg")
        }
    }
}
