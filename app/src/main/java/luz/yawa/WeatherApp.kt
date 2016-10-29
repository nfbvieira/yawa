package luz.yawa

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class WeatherApp : Application(){
    val requestQueue by lazy { Volley.newRequestQueue(this) }

    override fun onCreate(){
        super.onCreate()
        //create
    }

    val Application.requestQueue : RequestQueue
        get() = (this as WeatherApp).requestQueue
}