package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DairyCalculationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private MaterialEditText editTextNumDairyCows;
    private MaterialEditText editTextAverageMilkYield;
//    Button btnCalculate;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_calculation);
    editTextNumDairyCows = findViewById(R.id.editTextNumDairyCows);
    editTextAverageMilkYield = findViewById(R.id.editTextAverageMilkYield);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(DairyCalculationActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(DairyCalculationActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(DairyCalculationActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(DairyCalculationActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(DairyCalculationActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(DairyCalculationActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    public void calculateDairyEmissions(View view){

        String numDairyCows = editTextNumDairyCows.getText().toString();
        int numberDairyCows = Integer.parseInt(numDairyCows);
        String avgMilkYield = editTextAverageMilkYield.getText().toString();
        int averageMilkYield = Integer.parseInt(avgMilkYield);

       double oneDairyCowEmissionsPA =  averageMilkYield * 1039 / 1000;
       double totalDairyEmissionsPA = oneDairyCowEmissionsPA * numberDairyCows;

        Intent intent = new Intent(DairyCalculationActivity.this, DairyRecommendationsActivity.class);
        intent.putExtra("NumberDairyCows", numberDairyCows);
        intent.putExtra("AverageMilkYield", averageMilkYield);
        intent.putExtra("OneCowEmissionsPA", oneDairyCowEmissionsPA);
        intent.putExtra("TotalEmissionsPA", totalDairyEmissionsPA);
        startActivity(intent);

        System.out.println("One Dairy Emissions ***" + oneDairyCowEmissionsPA);
        System.out.println("Total Dairy Emissions ***" + totalDairyEmissionsPA);


        Toast.makeText(DairyCalculationActivity.this, "Total Dairy Emissions Per Annumn: " + totalDairyEmissionsPA, Toast.LENGTH_LONG);

    }
}
