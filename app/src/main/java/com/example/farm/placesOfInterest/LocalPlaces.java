package com.example.farm.placesOfInterest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.os.StrictMode;

import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.location.FusedLocationProviderClient;


public class LocalPlaces extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private RecyclerView mListView;
    private static final int REQUEST_LOCATION = 0;
    private static final String API_KEY = "AIzaSyDzyCapVx5jEfA3haEGUCI0jSIYFu3xUxI";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/textsearch";
    private static final String OUT_JSON = "/json?";
    private static final String LOG_TAG = "ListRest";
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    int count = 0;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_places);


        mListView = findViewById(R.id.alleyView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000000);
        mLocationRequest.setFastestInterval(100000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());



        //String lName = intent.getStringExtra("lastName");
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Double longitude = 0.0;
            Double latitude = 0.0;
            for (Location location : locationResult.getLocations()) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            int radius = 10;
            List<Place> list = search(longitude, latitude, radius);
            if (list != null) {
                PlacesAdapter adapter = new PlacesAdapter(list, LocalPlaces.this);

                RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
                mListView.setLayoutManager(reLayoutManager);
                mListView.setItemAnimator(new DefaultItemAnimator());
                mListView.setAdapter(adapter);
            } else {
                Toast.makeText(LocalPlaces.this, "No places nearby.", Toast.LENGTH_SHORT).show();
            }
        }


    };



    public ArrayList<Place> search ( double lat, double lng, int radius){
        ArrayList<Place> resultList = new ArrayList<>();

        Intent intent = getIntent();
        int value = intent.getIntExtra("key", 0);
        String result = "";
        if (value == 0) {
            result = "vets";
        } else if (value == 1) {
            result = "farm machinery";
        } else if (value == 2) {
            result = "agricultural feeds";
        } else if (value == 3) {
            result = "welders";
        } else if (value == 4) {
            result = "agricultural consultants";
        }

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("location=" + (lng) + "," + (lat));
            sb.append("&radius=" + (radius));
            sb.append("&query=" + (result));              //this query will change to whatever search item is selected etc.
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            resultList = new ArrayList<>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place(null, null, 0, null, null);
                place.name = predsJsonArray.getJSONObject(i).getString("name");
                place.rating = predsJsonArray.getJSONObject(i).getDouble("rating");
                place.location = predsJsonArray.getJSONObject(i).getString("formatted_address");
                place.placeID = predsJsonArray.getJSONObject(i).getString("place_id");
                String latitude = Double.toString(lat);
                String longitude = Double.toString(lng);
                place.userLocation = longitude + "," + latitude;
                if (count < 6) {
                    place.website = websiteMaker(place.placeID);
                }
                if (resultList.size() < 5) {
                    resultList.add(place);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public String websiteMaker (String placeID){
        HttpURLConnection conn = null;
        String webObj = null;
        StringBuilder jsonDetails = new StringBuilder();
        try {
            StringBuilder sb1 = new StringBuilder(PLACES_API_BASE);
            sb1.append(TYPE_DETAILS);
            sb1.append(OUT_JSON);
            sb1.append("place_id=" + placeID);
            sb1.append("&fields=website");
            sb1.append("&key=" + API_KEY);

            URL url = new URL(sb1.toString());
            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonDetails.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing API URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject reader = new JSONObject(jsonDetails.toString());
            JSONObject sys = reader.getJSONObject("result");
            webObj = sys.getString("website");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }
        count++;
        return webObj;
    }

}
