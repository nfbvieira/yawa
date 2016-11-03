package luz.yawa

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val URI = "http://api.openweathermap.org/data/2.5/weather?id=6458923"
    private val KEY = "&APPID=f3f9bd7053ff66839aae078ee1214109"
    private val ICON_URL = "http://openweathermap.org/img/w/"
    private val ICON_EXTENSION = ".png"
    private val LANG_URI = "&lang="
    private val TEMP_UNIT_URI = "&units="

    val city_view by lazy {findViewById(R.id.city_name) as TextView}
    val weather_icon_view by lazy {findViewById(R.id.weather_icon) as ImageView}
    val weather_info_view by lazy {findViewById(R.id.weather_info) as TextView}
    val current_temp_view by lazy {findViewById(R.id.curr_temp) as TextView}
    val max_temp_view by lazy {findViewById(R.id.max_temp) as TextView}
    val min_temp_view by lazy {findViewById(R.id.min_temp) as TextView}

    //var last_modified: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if(savedInstanceState == null) {

            var image: Bitmap? = null
            (application as WeatherApp).requestQueue.add(
                    JsonObjectRequest(
                            URI + KEY + LANG_URI + Locale.getDefault().language + TEMP_UNIT_URI + "metric",
                            null,
                            {
                                val city = it.get("name") as String
                                city_view.text = city

                                val weatherArray = it.get("weather") as JSONArray
                                val weather = weatherArray.getJSONObject(0)
                                var icon = weather.get("icon") as String
                                //var image_loader = DownloadImageTask(weather_icon_view).execute(ICON_URL + icon + ICON_EXTENSION)
                                //image = image_loader.get()
                                (application as WeatherApp).requestQueue.add(
                                        ImageRequest(
                                                ICON_URL + icon + ICON_EXTENSION,
                                                {
                                                    weather_icon_view.setImageBitmap(it)
                                                },
                                                0,
                                                0,
                                                null,
                                                null,
                                                {
                                                    Toast.makeText(this, "Failure to load icon", Toast.LENGTH_LONG).show()
                                                }
                                        )
                                )

                                weather_info_view.text = weather.get("description") as String

                                val weatherMain = it.get("main") as JSONObject
                                val d_format: DecimalFormat = DecimalFormat("#")
                                d_format.isDecimalSeparatorAlwaysShown = false
                                current_temp_view.text = d_format.format(weatherMain.get("temp") as Double) + "º"
                                max_temp_view.text = d_format.format(weatherMain.get("temp_max") as Int) + "º"    //was changed to Int in API
                                min_temp_view.text = d_format.format(weatherMain.get("temp_min") as Int) + "º"    //was changed to Int in API
                            },
                            {
                                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
                            }
                    )
            )
        }
        val nextDays = findViewById(R.id.days_info_button) as Button
        nextDays.setOnClickListener {
            val intent: Intent = Intent(this, WeeklyListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?){
        outState?.putString("city_name", city_view.text as String)
        outState?.putString("description", weather_info_view.text as String)
        outState?.putString("temp", current_temp_view.text as String)
        outState?.putString("temp_max", max_temp_view.text as String)
        outState?.putString("temp_min", min_temp_view.text as String)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?){
        super.onRestoreInstanceState(savedInstanceState)
        city_view.text = savedInstanceState?.getString("city_name")
        weather_info_view.text = savedInstanceState?.getString("description")
        current_temp_view.text = savedInstanceState?.getString("temp")
        max_temp_view.text = savedInstanceState?.getString("temp_max")
        min_temp_view.text = savedInstanceState?.getString("temp_min")
    }

    //classe a ser movida para a classe Utils
    //taken from StackOverflow
    public class DownloadImageTask(var bmImage: ImageView) :AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap {
            val urldisplay = urls[0]
            var mIcon: Bitmap? = null
            try{
                mIcon = BitmapFactory.decodeStream(URL(urldisplay).openStream())!!
            } catch (e: Exception){
                Log.e("Error", e.message)
            }
            if(mIcon == null) throw NullPointerException()
            return mIcon
        }

        override fun onPostExecute(result: Bitmap){
            bmImage.setImageBitmap(result)
        }
    }
}
