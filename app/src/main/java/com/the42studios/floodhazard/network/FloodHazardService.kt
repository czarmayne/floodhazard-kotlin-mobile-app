package com.the42studios.floodhazard.network

import com.the42studios.floodhazard.entity.Location
import com.the42studios.floodhazard.entity.Sensitivity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FloodHazardService {

    @GET("/location")
    abstract fun searchlocation(): Call<List<String>>

    @GET("/location/view")
    abstract fun searchlocationView(): Call<List<Location>>

    @GET("/forecast/warnings")
    fun getTodaysForecast(@Query("date") date: String): Call<List<Sensitivity>>
}