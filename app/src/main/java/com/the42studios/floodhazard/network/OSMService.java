package com.the42studios.floodhazard.network;

import com.the42studios.floodhazard.models.OSMData;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OSMService {

    @GET("/search")
    Call<List<OSMData>> searchAddress(@QueryMap Map<String, String> options);
}