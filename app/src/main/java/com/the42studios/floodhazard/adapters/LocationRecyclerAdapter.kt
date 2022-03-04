package com.the42studios.floodhazard.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.the42studios.floodhazard.EditLocationActivity
import com.the42studios.floodhazard.LocationActivity
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.entity.Location

import java.util.ArrayList

class LocationRecyclerAdapter(locationList: List<Location>, internal var context: Context) : RecyclerView.Adapter<LocationRecyclerAdapter.LocationViewHolder>(){

    internal var locationList: List<Location> = ArrayList()
    init {
        this.locationList = locationList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_location, parent, false)
        return LocationViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        Log.i("LOCATION RECYCLER","CREATING VIEW")
        val location = locationList[position]
        holder.name.text = location.name
        holder.details.text = location.details

        if (location.status == "Y")
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorSuccess)
        else
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorUnSuccess)

        holder.itemView.setOnClickListener {
            Log.i("LOCATION RECYCLER","START SET ITEM ON CLICK")
            val i = Intent(context, EditLocationActivity::class.java)
            i.putExtra("Mode", "E")
            i.putExtra("Id", location.id)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
            Log.i("LOCATION RECYCLER","DONE SET ITEM ON CLICK")
        }
        Log.i("LOCATION RECYCLER","DONE VIEW")
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.locName) as TextView
        var details: TextView = view.findViewById(R.id.locDesc) as TextView
        var list_item: LinearLayout = view.findViewById(R.id.list_item_location_db) as LinearLayout
    }

}
