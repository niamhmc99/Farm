package com.example.farm.weather.api;


import com.example.farm.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherService {

    //use a GET HTTP method request to get the data -- create query parm into URL to query the data -- pass in our latitude and longitude
    @GET("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(@Query("lat")String lat, @Query("lon") String lon, @Query("APPID") String appId);
}
