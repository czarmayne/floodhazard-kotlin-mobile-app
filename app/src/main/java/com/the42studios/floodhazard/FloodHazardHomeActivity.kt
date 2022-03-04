package com.the42studios.floodhazard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.util.CollectionUtils
import com.the42studios.floodhazard.activities.MainActivity
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Location
import com.the42studios.floodhazard.entity.Notification
import com.the42studios.floodhazard.entity.Sensitivity
import com.the42studios.floodhazard.network.FloodHazardService
import com.the42studios.floodhazard.network.RetrofitClientInstance
import com.the42studios.floodhazard.service.FloodNotificationJobService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FloodHazardHomeActivity : AppCompatActivity() {

    var TAG = "HOME ACTIVITY"
    var dbHandler: DatabaseHandler? = null
    var listLocation: List<Location> = ArrayList<Location>()
    var listSensitivity: List<Sensitivity> = ArrayList<Sensitivity>()
    val DATE_PATTERN = "yyyy-MM-dd"
    val HOUR_PATTERN = "HH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flood_hazard_home)

        locationWebServiceCall()
        forecastWebServiceCall()
        setForNotification()

        var btnSettings = findViewById(R.id.btn_settingsForFloodLocations) as Button
        var btnNotifi = findViewById(R.id.btn_settings) as Button
        var btnView = findViewById(R.id.btn_viewAllLocations) as Button

        btnNotifi.setOnClickListener {
            val intent = Intent(this@FloodHazardHomeActivity,SettingsActivity::class.java)
            startActivity(intent)
        }

        btnSettings.setOnClickListener {view ->
            val intent = Intent(this@FloodHazardHomeActivity, LocationActivity::class.java)
            startActivity(intent)
        }

        btnView.setOnClickListener {view ->
            val intent = Intent(this@FloodHazardHomeActivity, NotificationActivity::class.java)
            startActivity(intent)
        }
    }

    fun locationWebServiceCall() {
        Log.i(TAG, "START LOCATION WEB SERVICE CALL")

        dbHandler = DatabaseHandler(this)

        val floodHazardService: FloodHazardService = RetrofitClientInstance.getFloodHazardClient(dbHandler!!.getSettings()).create(FloodHazardService::class.java)

        val call = floodHazardService.searchlocationView()

        call.enqueue(object : Callback<List<Location>> {
            override fun onResponse(call: Call<List<Location>>, response: retrofit2.Response<List<Location>>) {
                Log.d(TAG, "[RESULT] : ${response.isSuccessful}")
                Log.d(TAG, "[BODY] : ${response.body().toString()}")

                val results: List<Location>? = response.body()

                if (results != null) {
                    for(result: Location in results) {
                        Log.d(TAG, "[PARSE RESULT] : ${result.toString()}")
                        dbHandler?.updateLocation(location = result);
                    }
                }

            }
            override fun onFailure(call: Call<List<Location>>, t: Throwable) {
                Log.d(TAG, "ERROR LOCATION WS ====================== \n ${t.printStackTrace()}")
                Toast.makeText(this@FloodHazardHomeActivity, "Can't load location...Please change your setting!", Toast.LENGTH_LONG).show()
            }
        })

        Log.i(TAG, "END OF LOCATION WEB SERVICE CALL")

    }

    fun setForNotification() {
        Log.i(TAG, "======================= SET FOR NOTIFICATION ")
        dbHandler = DatabaseHandler(this)
        listLocation = (dbHandler as DatabaseHandler).getNotify()
        if(!CollectionUtils.isEmpty(listLocation)) {
            var notificationList : List<Notification>  = (dbHandler as DatabaseHandler).getScheduleList()
            if(notificationList.isEmpty()) {
                Log.i(TAG, "======================= ADD FOR NOTIFICATION ")
                (dbHandler as DatabaseHandler).addSchedule()
                scheduleJob()
            }
        }
    }

    private fun scheduleJob() {
        val jobScheduler = getSystemService(
                Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        // The JobService that we want to run
        val name = ComponentName(this, FloodNotificationJobService::class.java)

        // Schedule the job
        val result = jobScheduler.schedule(getJobInfo(123, 1, name))

        // If successfully scheduled, log this thing
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Scheduled job successfully!")
            Toast.makeText(this@FloodHazardHomeActivity, "Flood Notification has been scheduled!", Toast.LENGTH_LONG).show()
        }

    }

    private fun getJobInfo(id: Int, hour: Long, name: ComponentName): JobInfo {
        val interval = TimeUnit.MINUTES.toMillis(hour) // run every hour
        val isPersistent = true // persist through boot
//        val networkType = JobInfo.NETWORK_TYPE_ANY // Requires some sort of connectivity

        val jobInfo: JobInfo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.i("JOB SCHEDULING", "Nougat and up ========== ")
            jobInfo = JobInfo.Builder(id, name)
//                    .setMinimumLatency(interval)
                    .setPeriodic(15 * 60 * 1000)
//                    .setRequiredNetworkType(networkType)
//                    .setBackoffCriteria(interval, 10 * 1000)
                    .setPersisted(isPersistent)
//                    .setOverrideDeadline(TimeUnit.HOURS.toMillis(hour))
                    .build()
        } else {
            jobInfo = JobInfo.Builder(id, name)
                    .setPeriodic(15 * 60 * 1000)
//                    .setRequiredNetworkType(networkType)
                    .build()
        }

        return jobInfo
    }

    fun forecastWebServiceCall() {
        Log.i(TAG, "START FORECAST WEB SERVICE CALL")
        dbHandler = DatabaseHandler(this)

        val floodHazardService: FloodHazardService = RetrofitClientInstance.getFloodHazardClient(dbHandler!!.getSettings()).create(FloodHazardService::class.java)

        val call = floodHazardService.getTodaysForecast(getDate())

        call.enqueue(object : Callback<List<Sensitivity>> {
            override fun onResponse(call: Call<List<Sensitivity>>, response: Response<List<Sensitivity>>) {
                Log.d(TAG, "[RESULT] : ${response.isSuccessful}")
                Log.d(TAG, "[BODY] : ${response.body().toString()}")
                dbHandler?.deleteAllSensitivity(getDate())
                val results: List<Sensitivity>? = response.body()
                Log.d(TAG, "[BODY SIZE] : ${results!!.size}")
                if (results != null) {
                    for(result: Sensitivity in results) {
                        Log.d(TAG, "[TEST PARSE RESULT] : ${result.toString()}")
                        dbHandler?.addSensitivity(sensitivity = result);
                    }
                }
            }
            override fun onFailure(call: Call<List<Sensitivity>>, t: Throwable) {
                Log.d(TAG, "ERROR FORECAST WS ====================== \n ${t.printStackTrace()}")
                Toast.makeText(this@FloodHazardHomeActivity, "Can't load today's forecast...Please change your setting!", Toast.LENGTH_LONG).show()
            }
        })
        Log.i(TAG, "END FORECAST WEB SERVICE CALL")
    }

    fun getWarningNotification() : List<Sensitivity> {
        Log.i("getWarningNotification", "START=======================")
        dbHandler = DatabaseHandler(this)
        var hr = getHour() + 2
        listSensitivity = (dbHandler as DatabaseHandler).getSensitivityByDateAndHours(getDate(),hr , hr, true)
        return listSensitivity
        Log.i("getWarningNotification", "END=======================")
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

    override fun onResume() {
        super.onResume()
        locationWebServiceCall()
        getWarningNotification()
        setForNotification()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
