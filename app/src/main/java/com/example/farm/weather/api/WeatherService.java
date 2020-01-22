package com.example.farm.weather.api;


import com.example.farm.weather.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.Query;


public interface WeatherService {

    //pass in our latitude and longitude
    Call<WeatherResponse> getCurrentWeatherData(@Query("lat")String lat, @Query("lon") String lon, @Query("APPID") String appId);
}
