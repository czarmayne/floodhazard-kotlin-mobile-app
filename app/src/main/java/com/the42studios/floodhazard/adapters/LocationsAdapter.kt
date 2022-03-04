package com.the42studios.floodhazard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.the42studios.floodhazard.R
import com.the42studios.floodhazard.models.FloodData

class LocationsAdapter(context: Context, private var values: ArrayList<FloodData>) :
        ArrayAdapter<FloodData>(context, R.layout.list_item_view, values){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false)
        val streetNameView: TextView = rowView.findViewById(R.id.text_list_streetName)
        val descriptionView: TextView = rowView.findViewById(R.id.text_list_description)

        streetNameView.text = values[position].getStreetName()
        descriptionView.text = values[position].getDescription()
        return rowView
    }
}