package com.the42studios.floodhazard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.the42studios.floodhazard.db.DatabaseHandler
import com.the42studios.floodhazard.entity.Settings

class SettingsActivity : AppCompatActivity() {

    var TAG = "SETTINGS ACTIVITY"
    var dbHandler: DatabaseHandler? = null
    var defaultURL = "https://floodhazard.herokuapp.com/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var  inputUrl = findViewById(R.id.input_url) as EditText
        Log.i(TAG, "INPUT URL SETTING ===== "+inputUrl.text)
        var  viewUrl = findViewById(R.id.view_url) as TextView
        var btnSaveUrl = findViewById(R.id.btn_save_url) as Button
        var btnDefaultUrl = findViewById(R.id.btn_default_url) as Button

        dbHandler = DatabaseHandler(this)
        viewUrl.text = dbHandler!!.getSettings()
        btnSaveUrl.setOnClickListener {
            dbHandler!!.deleteSettings()
            var settings = Settings()
            if(inputUrl.text.toString().isNotBlank())
                settings.url = inputUrl.text.toString()
            else
                settings.url = defaultURL;
            Log.i(TAG, "Settings ========================== "+ settings.toString())
            dbHandler!!.addSettings(settings)
            viewUrl.text = settings.url
            Toast.makeText(this@SettingsActivity, "Settings has been updated!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SettingsActivity,FloodHazardHomeActivity::class.java)
            startActivity(intent)
        }

        btnDefaultUrl.setOnClickListener {
            dbHandler = DatabaseHandler(this)
            dbHandler!!.deleteSettings()
            var settings = Settings()
            settings.url = defaultURL;
            Log.i(TAG, "Settings ========================== "+ settings.toString())
            dbHandler!!.addSettings(settings)
            viewUrl.text = settings.url
            Toast.makeText(this@SettingsActivity, "Settings has been updated!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SettingsActivity,FloodHazardHomeActivity::class.java)
            startActivity(intent)
        }

    }
}
