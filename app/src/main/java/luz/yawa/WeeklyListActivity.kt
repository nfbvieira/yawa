package luz.yawa

import android.app.ListActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class WeeklyListActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_list)
    }
}
