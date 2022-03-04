package com.the42studios.floodhazard.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.the42studios.floodhazard.R

import java.util.concurrent.TimeUnit

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        Log.i("TEST MAIN", "ACT")
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                scheduleJob()
            }
        })
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
        }

    }

    private fun getJobInfo(id: Int, hour: Long, name: ComponentName): JobInfo {
        val interval = TimeUnit.MINUTES.toMillis(hour) // run every hour
        val isPersistent = true // persist through boot
        val networkType = JobInfo.NETWORK_TYPE_ANY // Requires some sort of connectivity

        val jobInfo: JobInfo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = JobInfo.Builder(id, name)
                    .setMinimumLatency(interval)
                    .setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build()
        } else {
            jobInfo = JobInfo.Builder(id, name)
                    .setPeriodic(interval)
                    .setRequiredNetworkType(networkType)
                    .setPersisted(isPersistent)
                    .build()
        }

        return jobInfo
    }

    companion object {

        private val TAG = Main2Activity::class.java.simpleName
    }

}
