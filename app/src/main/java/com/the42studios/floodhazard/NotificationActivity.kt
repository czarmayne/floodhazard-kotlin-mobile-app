package com.the42studios.floodhazard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.the42studios.floodhazard.adapter.SensitivityRecyclerAdapter
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Location
import com.the42studios.floodhazard.entity.Sensitivity
import com.the42studios.floodhazard.models.OSMData
import com.the42studios.floodhazard.network.FloodHazardService
import com.the42studios.floodhazard.network.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class NotificationActivity : AppCompatActivity() {

    var notificationRecyclerAdapter: SensitivityRecyclerAdapter? = null;
    var recyclerView: RecyclerView? = null
    var dbHandler: DatabaseHandler? = null
    var listSensitivity: List<Sensitivity> = ArrayList<Sensitivity>()
    var linearLayoutManager: LinearLayoutManager? = null
    val DATE_PATTERN = "yyyy-MM-dd"
    val HOUR_PATTERN = "HH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        Log.i("INIT DB ", "on create for notification")
        initViews()
    }

    fun initDB4Hrs() {
        Log.i("initDB4Hrs PARAM", "=======================")
        dbHandler = DatabaseHandler(this)
        var endHr = getHour() + 6
        listSensitivity = (dbHandler as DatabaseHandler).getSensitivityByDateAndHours(getDate(),getHour() , endHr, false)
        Log.d("NOTIF RESULT", "================"+Arrays.deepToString(listSensitivity.toTypedArray()))

        notificationRecyclerAdapter = SensitivityRecyclerAdapter(sensitivityList = listSensitivity, context = this)
        (recyclerView as RecyclerView).adapter = notificationRecyclerAdapter
        Log.i("END initDB4Hrs PARAM", "=======================")
    }

    fun initViews() {
        Log.i("INIT NOTIFICATIONVW", "starting to show views=======================")

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_notif)
        notificationRecyclerAdapter = SensitivityRecyclerAdapter(sensitivityList = listSensitivity, context = applicationContext)

        linearLayoutManager = LinearLayoutManager(applicationContext)
        (recyclerView as RecyclerView).layoutManager = linearLayoutManager
        Log.i("END INIT NOTIFICATIONVW", "starting to show views=======================")
    }

    override fun onResume() {
        super.onResume()
        initDB4Hrs()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getDate(): String {
        val dateFormat = SimpleDateFormat(DATE_PATTERN)
        val date = Date()
        Log.d(" Date Now {}", dateFormat.format(date))
        return dateFormat.format(date)
    }

    fun getHour(): Int {
        val dateFormat = SimpleDateFormat(HOUR_PATTERN)
        val date = Date()
        Log.d(" Hour Now {}", dateFormat.format(date))
        return Integer.parseInt(dateFormat.format(date))
    }
}
