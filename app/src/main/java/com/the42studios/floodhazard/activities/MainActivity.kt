package com.the42studios.floodhazard.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.adapters.LocationsAdapter
import com.the42studios.floodhazard.models.FloodData
import com.the42studios.floodhazard.models.OSMData
import com.the42studios.floodhazard.network.OSMService
import com.the42studios.floodhazard.network.RetrofitClientInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java!!.getName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floodDataItems: ArrayList<FloodData> = sampleFloodDataList()
        val listView: ListView = findViewById(R.id.locations) as ListView
        listView.adapter = LocationsAdapter(this, floodDataItems)
        listView.setOnItemClickListener { parent, view, position, id ->

            val floodData: FloodData = floodDataItems[position]

            Log.d(TAG, "[VIEW_LOCATION] streetName: ${floodData.getStreetName()}, description: ${floodData.getDescription()}")

            val osmService: OSMService = RetrofitClientInstance.getRetrofitInstance().create(OSMService::class.java)

            val searchOptions = hashMapOf(
                    "q" to "${floodData.getStreetName()}, Manila",
                    "format" to "json",
                    "polygon" to "0",
                    "addressdetails" to "0",
                    "limit" to "1")

            //q=blumentritt st manila&format=json&polygon=0&addressdetails=1&limit=1&dedup=1

            Log.d(TAG, "[QUERY] : ${searchOptions.toString()}")
            val call = osmService.searchAddress(searchOptions)
            call.enqueue(object : Callback<List<OSMData>> {
                override fun onResponse(call: Call<List<OSMData>>, response: Response<List<OSMData>>) {
                    Log.d(TAG, "[RESULT] : ${response.isSuccessful}")

                    val osmData = response.body() as List<OSMData>

                    if(!osmData.isEmpty()){
                        val geodata = osmData[0]

                        Log.d(TAG, "[display name] : ${geodata.displayName}")

                        val intent = Intent(this@MainActivity, ViewLocationActivity::class.java)
                        intent.putExtra("osm", geodata)

                        startActivity(intent)
                    }else{
                        Toast.makeText(this@MainActivity, "Sorry. Can't determine the location", Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call<List<OSMData>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    private fun sampleFloodDataList(): ArrayList<FloodData>{
        val data = ArrayList<FloodData>()
        data.add(FloodData("Blumentritt", "Maria Clara St to Calamba St"))
        data.add(FloodData("Dimasalang Ave", "Makiling St to Retiro St. / Maceda St"))
        data.add(FloodData("Espana Blvd", "Antipolo St to Blumentritt"))
        data.add(FloodData("Legarda St", "Corner Gastambide St"))
        data.add(FloodData("V. Mapa St", "Guadal Canal St to Old Sta. Mesa St"))

        return data
    }
}
