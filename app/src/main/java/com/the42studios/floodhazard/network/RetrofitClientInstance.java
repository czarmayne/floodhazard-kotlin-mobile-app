package com.the42studios.floodhazard.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static Retrofit retrofit_ws;
    private static final String BASE_URL = "https://nominatim.openstreetmap.org";
    private static final String FLOODHAZARD_BASE_URL = "https://floodhazard.herokuapp.com/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getFloodHazardClient(String url) {
        if (retrofit_ws == null) {
            String urlBase = !url.isEmpty() ? url : FLOODHAZARD_BASE_URL;
            System.out.print("URL BASE FOR WS : "+ urlBase);
            retrofit_ws = new Retrofit.Builder()
                    .baseUrl(urlBase)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_ws;
    }
}