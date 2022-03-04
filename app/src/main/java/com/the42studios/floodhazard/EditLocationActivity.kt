package com.the42studios.floodhazard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
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

class EditLocationActivity : AppCompatActivity() {

    var dbHandler: DatabaseHandler? = null
    var isEditMode = false
    var TAG = "EDIT LOCATION"
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.i("EDIT LOCATION == ", "Start Edit Activity")
        initDB()
        initOperations()

    }

    private fun initDB() {
        dbHandler = DatabaseHandler(this)
        Log.d("EDIT LOCATION == ", (intent.getStringExtra("Mode") == "E").toString());
        if (intent != null && intent.getStringExtra("Mode") == "E") {
            isEditMode = true
            val location: Location = dbHandler!!.getLocationList(intent.getIntExtra("Id",0))
            edit_locName.setText(location.name)
            edit_locDesc.setText(location.details)
            swt_notify.isChecked = location.status == "Y"
        }
    }

    private fun initOperations() {
        btn_delete.setOnClickListener({
            val dialog = AlertDialog.Builder(this).setTitle("Info").setMessage("Click 'YES' Delete the Location.")
                    .setPositiveButton("YES", { dialog, i ->
                        val success = dbHandler?.deleteLocation(intent.getIntExtra("Id", 0)) as Boolean
                        if (success)
                            finish()
                        dialog.dismiss()
                    })
                    .setNegativeButton("NO", { dialog, i ->
                        dialog.dismiss()
                    })
            dialog.show()
        })
        btn_save.setOnClickListener({
            var success: Boolean = false
            if (!isEditMode) {
                val location: Location = Location()
                location.name = edit_locName.text.toString()
                location.details = edit_locDesc.text.toString()
                Log.i("EDIT LOCATION", "NOTIFY? "+ swt_notify.isChecked)
                if (swt_notify.isChecked)
                    location.status = "Y"
                else
                    location.status = "N"
                success = dbHandler?.updateLocation(location) as Boolean
            } else {
                val location: Location = Location()
                location.id = intent.getIntExtra("Id", 0)
                location.name = edit_locName.text.toString()
                location.details = edit_locDesc.text.toString()
                Log.i("EDIT LOCATION", "NOTIFY? "+ swt_notify.isChecked)
                if (swt_notify.isChecked)
                    location.status = "Y"
                else
                    location.status = "N"

                Log.d("UPDATE", location.toString())
                success = dbHandler?.updateLocation(location) as Boolean
            }

            if (success)
                finish()
        })

        btn_viewmap.setOnClickListener({
            Log.d(TAG, "[VIEW_LOCATION] streetName: ${edit_locName.text.toString()}, description: ${edit_locDesc.text.toString()}")

            val osmService: OSMService = RetrofitClientInstance.getRetrofitInstance().create(OSMService::class.java)

            val searchOptions = hashMapOf(
                    "q" to "${edit_locName.text.toString()}, Manila",
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

                        val intent = Intent(this@EditLocationActivity, ViewLocationActivity::class.java)
                        intent.putExtra("osm", geodata)

                        startActivity(intent)
                    }else{
                        Toast.makeText(this@EditLocationActivity, "Sorry. Can't determine the location", Toast.LENGTH_SHORT).show()
                    }

                }
                override fun onFailure(call: Call<List<OSMData>>, t: Throwable) {
                    Log.d("ERROR", "${t.printStackTrace()}")
                    Toast.makeText(this@EditLocationActivity, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show()
                }
            })
        })


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
