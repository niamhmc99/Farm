package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.ToDoListActivity;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmissionsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ImageButton imageButtonBeef, imageButtonDairy;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissions);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(EmissionsActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(EmissionsActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(EmissionsActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(EmissionsActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(EmissionsActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(EmissionsActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    public void clickBeef(View view){
        imageButtonBeef = findViewById(R.id.imageButtonBeef);
        imageButtonBeef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmissionsActivity.this, BeefCalculationActivity.class));

            }
        });

    }

    public void clickDairy(View view){
        imageButtonDairy = findViewById(R.id.imageButtonDairy);
        imageButtonDairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmissionsActivity.this, DairyCalculationActivity.class));

            }
        });
    }
}
