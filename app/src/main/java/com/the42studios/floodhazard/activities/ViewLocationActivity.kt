package com.the42studios.floodhazard.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.models.OSMData


class ViewLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = ViewLocationActivity::class.java!!.getName()
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_location)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val osm: OSMData = intent.extras.get("osm") as OSMData

        var lat = osm.lat.toDouble()
        var lon = osm.lon.toDouble()

        val coordinates = LatLng(lat, lon)

        mMap.addMarker(MarkerOptions().position(coordinates).title(osm.displayName))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f))

        this.title = osm.displayName
    }
}
