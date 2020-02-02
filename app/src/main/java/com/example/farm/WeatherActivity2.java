package com.example.farm;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherActivity2 extends AppCompatActivity {

    String CITY = "Dublin,IE"; //value of our search query {city, country code} is the parm used
    String API = "3e73a57a2c75697757c6110ad50aa6da"; //key got from API
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather2);


        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);

        new weatherTask().execute();

        // use to get lat and lon
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000000);
//        mLocationRequest.setFastestInterval(100000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() { //shows something before doInBackground
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
      //      findViewById(R.id.loader).setVisibility(View.VISIBLE);
//            findViewById(R.id.mainContainer).setVisibility(View.GONE);
//            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            //Async Task allows to perform background operations and ease use of UI thread
            //need to get weather info from API --make http request --use Async Task to do this
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);

            Log.d("Response url", String.valueOf(response));
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
//        update ui of background operation result.
            //response from the url is passed here. It's in JSON Format -- extract data

            try {
                JSONObject jsonObj = new JSONObject(result); // parent is {
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0); //array as weather elements inside [

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd-MM-yyyy HH:mm ", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp");
                String tempMin =  main.getString("temp_min");
                String tempMax =  main.getString("temp_max");
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");


                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                Log.d("Address", address);
                Log.d("Temp", temp);
                Log.d("Temp MIN", tempMin);
                Log.d("Sunrise", String.valueOf(sunrise));
                Log.d("wind speed", windSpeed);
                Log.d("Address", address);

                temp= Math.round(Float.parseFloat(temp))+ "°C";
                tempMin = "Min Temp: " + Math.round(Float.parseFloat(tempMin)) + "°C";
                tempMax = "Max Temp: " + Math.round(Float.parseFloat(tempMax)) + "°C";

                // Populating the extracted data into our text views in the activity
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date(sunrise * 1000)));//format the timestamp into our desired format.
                sunsetTxt.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}