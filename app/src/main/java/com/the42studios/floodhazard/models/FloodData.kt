package com.the42studios.floodhazard.models

import java.io.Serializable

class FloodData constructor(streetName: String, description: String): Serializable{

    private var streetName: String = streetName
    private var description: String = description

    fun getStreetName(): String{
        return streetName
    }

    fun getDescription(): String{
        return description
    }
}