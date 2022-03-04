package com.the42studios.floodhazard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Location
import com.the42studios.floodhazard.models.OSMData
import com.the42studios.floodhazard.network.OSMService
import com.the42studios.floodhazard.network.RetrofitClientInstance
import kotlinx.android.synthetic.main.activity_edit_location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailForecastActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var isEditMode = false
    var TAG = "VIEW DETAIL FORECAST"
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_forecast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i(TAG, "Start View Detail Activity")
        initDB()

    }

    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        Log.d(TAG, (intent.getStringExtra("sensitivityLevel")).toString());
        Log.d(TAG, (intent.getStringExtra("sensitivityDetail")).toString());
        var impact = findViewById(R.id.impact) as TextView
        var affected = findViewById(R.id.affected) as TextView
        var location = findViewById(R.id.location_detail) as TextView
        var time = findViewById(R.id.time) as TextView
        var image = findViewById(R.id.imageViewDetail) as ImageView

        if(intent.getStringExtra("image").equals("car",true)) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.car, null));
        } else if(intent.getStringExtra("image").equals("kids",true)) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.kids, null));
        } else if(intent.getStringExtra("image").equals("rain",true)) {
            image.setImageDrawable(getResources().getDrawable(R.drawable.rain, null));
        }

        impact.setText(intent.getStringExtra("sensitivityDetail"))
        affected.setText(intent.getStringExtra("sensitivityLevel"))
        location.setText(intent.getStringExtra("location"))
        time.setText(intent.getStringExtra("time"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
