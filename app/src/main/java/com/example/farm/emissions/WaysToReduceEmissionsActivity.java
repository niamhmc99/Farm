package com.example.farm.emissions;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.EmissionData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WaysToReduceEmissionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView textTitle;
    private TextView textDescription;
    private TextView textMoreDetail;
    private EmissionData emissionData;
    private String fileName;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ways_to_reduce_emissions);
        fileName = getIntent().getStringExtra("fileName");
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textMoreDetail = findViewById(R.id.textMoreDetail);
        textMoreDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(WaysToReduceEmissionsActivity.this, emissionData.getPdfUrl());
            }
        });
        findViewById(R.id.textVisitWebsite).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(WaysToReduceEmissionsActivity.this, "https://www.teagasc.ie/about/our-organisation/connected/online-tools/carbon-navigator/?fbclid=IwAR0ATvfWzYwu8UacMOSD-ebMWJXOq9g7kV_ysXrNHxvMGddm4cXt6a1uY44");
            }
        });
        emissionData = loadFile(fileName);
        textTitle.setText(emissionData.getTitle());
        textDescription.setText(getNormalizeMessage(new Gson().toJson(emissionData.getDetail())));

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_emissions);
        bottomNavigationView.setOnNavigationItemSelectedListener(WaysToReduceEmissionsActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_home:
                Intent intent0 = new Intent(WaysToReduceEmissionsActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(WaysToReduceEmissionsActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(WaysToReduceEmissionsActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(WaysToReduceEmissionsActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(WaysToReduceEmissionsActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    public EmissionData loadFile(String fileName) {
        String jsonStr = loadJSONFromAsset(fileName, this);
        return new Gson().fromJson(jsonStr, EmissionData.class);
    }

    public String loadJSONFromAsset(String fileName, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String getNormalizeMessage(String response) {
        return response.replace("[", "").replace("]", "").replace("\"", "").replace(",", "\n\n");
    }

    private void openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }
}
