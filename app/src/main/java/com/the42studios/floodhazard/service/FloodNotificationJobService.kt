package com.the42studios.floodhazard.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.os.HandlerThread
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.the42studios.floodhazard.NotificationActivity
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Sensitivity
import java.text.SimpleDateFormat
import java.util.*


class FloodNotificationJobService : JobService() {

    var isWorking = false
    var jobCancelled = false
    private val channelId = "com.the42studios.floodhazard"
    private val defaultDescription = "Will there be flood in your area?"
    private val ticker  = "View Flood Impact on Flooded Areas"
    private val title = "Flood Hazard for the next 2 hours"
    var dbHandler: DatabaseHandler? = null
    var listSensitivity: List<Sensitivity> = ArrayList<Sensitivity>()
    val DATE_PATTERN = "yyyy-MM-dd"
    val HOUR_PATTERN = "HH"

    override fun onStartJob(params: JobParameters): Boolean {

        val handlerThread = HandlerThread("FloodHazardNotification")
        handlerThread.start()

        val handler = Handler(handlerThread.looper)
        handler.post(Runnable {
            Log.e(TAG, "Running!!!!!!!!!!!!!")
            notifyTest()
            Log.e(TAG, "Running End!!!!!!!!!!!!!")
            jobFinished(params, true)
        })

        return true
    }

    fun getDescription() : String {
        Log.i("getDescription", "START=======================")
        dbHandler = DatabaseHandler(this)
        var endhr = getHour() + 2
        var starthr = getHour() + 1
        var date = getDate()
        listSensitivity = (dbHandler as DatabaseHandler).getSensitivityByDateAndHours(date, starthr, endhr, true)
        Log.i("LIST OF SENSITIVITY", "listSensitivity"+listSensitivity)
        var desc : String = ""
        for(i in listSensitivity) {
            Log.i("LOCATION DESCRIPTION", "add === "+i.location)
            desc = desc + " , " + i.location
            Log.i("DESCRITPION", desc)
        }
        desc = desc.substring(2)
        Log.i("getDescription", desc + " END=======================")
        return "$date ($starthr - $endhr) : "+desc
    }

    fun notifyTest() {
        Log.d("======","BTN NOTIFY ===================================== ");
        Log.i(TAG, "ON RUN JOB ==================================================== ")

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this,NotificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        var builder : NotificationCompat.Builder
        var description : String? = getDescription()

        if(description.isNullOrBlank())
            description = defaultDescription

        Log.i(TAG, "DESCRIPTION ==================================================== \n "+defaultDescription)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("======","IF NOTIFY ===================================== ");
                var notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)

                // Configure the notification channel.
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)


                builder = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.notification_icon_background)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher_background))
                        .setContentTitle(title)
                        .setContentText(description)
                        .setTicker(ticker)
                        .setColor(Color.RED)
                        .setLights(0xFFFFA500.toInt(),800,1000)
                        .setDefaults(2)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
//                    .setWhen(System.currentTimeMillis())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
            } else {
                Log.d("======","ELSE NOTIFY ===================================== ");
                builder = NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon_background)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher_background))
//                        .setContent(contentView)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setTicker(ticker)
                        .setColor(Color.RED)
//                    .setWhen(System.currentTimeMillis())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            }

            notificationManager.notify(System.currentTimeMillis().toInt(),builder.build())

        Log.i(TAG, "DONE RUN JOB SCHEDULE ==================================================== ")

    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.d(TAG, "Job cancelled before being completed.")
        jobCancelled = true
        val needsReschedule = isWorking
        jobFinished(params, needsReschedule)
        return needsReschedule
    }

    companion object {

        private val TAG = FloodNotificationJobService::class.java.simpleName
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