package com.the42studios.floodhazard.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.the42studios.floodhazard.DetailForecastActivity
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.entity.Sensitivity

import java.util.ArrayList

class SensitivityRecyclerAdapter(sensitivityList: List<Sensitivity>, internal var context: Context) : RecyclerView.Adapter<SensitivityRecyclerAdapter.SensitivityViewHolder>() {

    internal var sensitivityList: List<Sensitivity> = ArrayList()

    init {
        this.sensitivityList = sensitivityList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensitivityViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_sensitivity, parent, false)
        return SensitivityViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: SensitivityViewHolder, position: Int) {
        Log.d("BIND","==========================")
        val sensitivity = sensitivityList[position]
        holder.location.text = sensitivity.location
        holder.dateHour.text = sensitivity.date +" "+ sensitivity.hour.toString() + ":00"
        holder.sensitivityDetail.text = sensitivity.sensitivityLevel
        Log.d("BIND COLOR", sensitivity.sensitivityDetail)
        if(sensitivity.sensitivityDetail.equals("RED", true)) {
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorLevelFour)
        } else if (sensitivity.sensitivityDetail.equals("ORANGE", true)) {
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorLevelThree)
        } else if (sensitivity.sensitivityDetail.equals("YELLOW", true)) {
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorLevelTwo)
        } else if (sensitivity.sensitivityDetail.equals("GREEN", true)) {
            holder.list_item.background = ContextCompat.getDrawable(context, R.color.colorLevelOne)
        }

        holder.itemView.setOnClickListener {
            Log.i("SENSITIVITY RECYCLER","START SET ITEM ON CLICK")
            val i = Intent(context, DetailForecastActivity::class.java)
            i.putExtra("sensitivityLevel", sensitivity.sensitivityLevel)
            i.putExtra("sensitivityDetail", sensitivity.sensitivityDetail)
            i.putExtra("location", sensitivity.location)
            i.putExtra("time", sensitivity.date +" "+ sensitivity.hour.toString() + ":00")
            if(sensitivity.sensitivityDetail.equals("RED", true)) {
                i.putExtra("image", "car")
            } else if (sensitivity.sensitivityDetail.equals("ORANGE", true)) {
                i.putExtra("image", "kids")
            } else if (sensitivity.sensitivityDetail.equals("YELLOW", true)) {
                i.putExtra("image", "rain")
            } else if (sensitivity.sensitivityDetail.equals("GREEN", true)) {
                i.putExtra("image", "rain")
            }
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
            Log.i("SENSITIVITY RECYCLER","DONE SET ITEM ON CLICK")
        }

    }

    override fun getItemCount(): Int {
        return sensitivityList.size
    }

    inner class SensitivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var location: TextView = view.findViewById(R.id.sen_location) as TextView
        var dateHour: TextView = view.findViewById(R.id.sen_dateHour) as TextView
        var sensitivityDetail: TextView = view.findViewById(R.id.sen_detail) as TextView
        var list_item: LinearLayout = view.findViewById(R.id.list_item_sensitivity_db) as LinearLayout
    }

}
