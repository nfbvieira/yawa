package luz.yawa

import android.app.ListActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*

class WeeklyListActivity : ListActivity() {

    private val URI = "http://api.openweathermap.org/data/2.5/forecast?id=6458923"
    private val KEY = "&APPID=f3f9bd7053ff66839aae078ee1214109"
    private val ICON_URL = "http://openweathermap.org/img/w/"
    private val ICON_EXTENSION = ".png"
    private var list : List<Map<String, Any>>? = null
    private var LANG_URI = "&lang="
    private var TEMP_UNIT = "&units="

    data class WeatherInfo(val icon:String, val description :String,
                           val curr_temp :Double, val max_temp :Double, val min_temp :Double)

    class WeatherDaysHolder(val iconView :ImageView, val descView :TextView,
                            val currTempView :TextView, val maxTempView :TextView, val minTempView :TextView)

    private var theDaysInfo :Array<WeatherInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_list)
        //adicionar a uma classe Utils
        operator fun JSONArray.iterator() =
                (0 until length()).asSequence().map { idx -> get(idx) as JSONObject }.iterator()

        (application as WeatherApp).requestQueue.add(
                JsonObjectRequest(
                        URI + KEY + LANG_URI + Locale.getDefault().language + TEMP_UNIT + "metric",
                        null,
                        {
                            val days_list = it.get("list") as JSONArray
                            val days_info = days_list.iterator().asSequence().map {
                                WeatherInfo((it.get("weather") as JSONArray).getJSONObject(0).get("icon") as String,
                                        (it.get("weather") as JSONArray).getJSONObject(0).get("description") as String,
                                        (it.get("main") as JSONObject).get("temp") as Double,
                                        (it.get("main") as JSONObject).get("temp_min") as Double,
                                        (it.get("main") as JSONObject).get("temp_max") as Double)
                            }.toList().toTypedArray()

                            theDaysInfo = days_info

                            val list_layout = R.layout.list_day_info
                            val inflater = LayoutInflater.from(this)
                            val adapter = object : ArrayAdapter<WeatherInfo>(this, list_layout, days_info){
                                override fun getView(position: Int, convertView :View?, parent : ViewGroup?) : View {
                                    val item_view = convertView ?:
                                            inflater.inflate(list_layout, parent, false)
                                                    /*.withTag({
                                                        WeatherDaysHolder(
                                                                //it.findViewById(R.id.icon1) as ImageView,
                                                                it.findViewById(R.id.text1) as TextView,
                                                                it.findViewById(R.id.text1) as TextView,
                                                                it.findViewById(R.id.text1) as TextView,
                                                                it.findViewById(R.id.text1) as TextView
                                                        )
                                                    })*/

                                    var view_holder = WeatherDaysHolder(
                                            item_view.findViewById(R.id.list_weather_icon) as ImageView,
                                            item_view.findViewById(R.id.list_weather_info) as TextView,
                                            item_view.findViewById(R.id.list_curr_temp) as TextView,
                                            item_view.findViewById(R.id.list_max_temp) as TextView,
                                            item_view.findViewById(R.id.list_min_temp) as TextView
                                    )
                                    val day_weather = days_info[position]

                                    val d_format : DecimalFormat = DecimalFormat("#")
                                    d_format.isDecimalSeparatorAlwaysShown = false

                                    MainActivity.DownloadImageTask(view_holder.iconView)
                                            .execute(ICON_URL + day_weather.icon + ICON_EXTENSION)
                                    view_holder.descView.text = day_weather.description
                                    view_holder.currTempView.text = d_format.format(day_weather.curr_temp ) + "º"
                                    view_holder.maxTempView.text = d_format.format(day_weather.max_temp) + "º"
                                    view_holder.minTempView.text = d_format.format(day_weather.min_temp) + "º"

                                    return item_view
                                }
                            }
                            listView.adapter = adapter
                        },
                        {
                            Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show()
                        }
                )
        )

    }
}

fun View.withTag(tagFactory : (View) -> Any) : View {
    this.tag = tagFactory(this)
    return this
}
