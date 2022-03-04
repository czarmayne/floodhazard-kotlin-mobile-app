package com.the42studios.floodhazard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.the42studios.floodhazard.adapter.LocationRecyclerAdapter
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Location

class LocationActivity : AppCompatActivity() {

    var TAG = "LOCATION ACTIVITY"
    var locationRecyclerAdapter: LocationRecyclerAdapter? = null;
    var recyclerView: RecyclerView? = null
    var dbHandler: DatabaseHandler? = null
    var listLocation: List<Location> = ArrayList<Location>()
    var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        Log.i("INIT DB ", "on create for locations")
        initViews()
//        initDB()

    }

    fun initDB() {
        Log.i(TAG, "======================= START DATABASE INIT")
        dbHandler = DatabaseHandler(this)
        listLocation = (dbHandler as DatabaseHandler).getLocationList()
        locationRecyclerAdapter = LocationRecyclerAdapter(locationList = listLocation, context = applicationContext)
        Log.i(TAG, "======================= BIND DATA TO VIEW")
        (recyclerView as RecyclerView).adapter = locationRecyclerAdapter
    }

    fun initViews() {
        Log.i(TAG, "starting to show views=======================")

        recyclerView = findViewById(R.id.recycler_view_loc) as RecyclerView
        locationRecyclerAdapter = LocationRecyclerAdapter(locationList = listLocation, context = applicationContext)

        linearLayoutManager = LinearLayoutManager(applicationContext)
        (recyclerView as RecyclerView).layoutManager = linearLayoutManager
        Log.i(TAG, "starting to show views=======================")

    }

    override fun onResume() {
        super.onResume()
        initDB()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
