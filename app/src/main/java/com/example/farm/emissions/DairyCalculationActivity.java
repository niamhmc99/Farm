package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DairyCalculationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private MaterialEditText editTextNumDairyCows;
    private MaterialEditText editTextAverageMilkYield;
    BottomNavigationView bottomNavigationView;
    private ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_calculation);
        editTextNumDairyCows = findViewById(R.id.editTextNumDairyCows);
        editTextAverageMilkYield = findViewById(R.id.editTextAverageMilkYield);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_emissions);
        bottomNavigationView.setOnNavigationItemSelectedListener(DairyCalculationActivity.this);
        constraintLayout = findViewById(R.id.constraintLayoutDairy);
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
        String avgMilkYield = editTextAverageMilkYield.getText().toString();

        if(!hasValidationErrors(numDairyCows, avgMilkYield)){
        int numberDairyCows = Integer.parseInt(numDairyCows);

        int averageMilkYield = Integer.parseInt(avgMilkYield);

       double oneDairyCowEmissionsPA =  averageMilkYield * 1309 / 1000;
       double totalDairyEmissionsPA = oneDairyCowEmissionsPA * numberDairyCows;

           Intent intent = new Intent(DairyCalculationActivity.this, DairyEmissionResultActivity.class);
           intent.putExtra("NumberDairyCows", numberDairyCows);
           intent.putExtra("AverageMilkYield", averageMilkYield);
           intent.putExtra("OneCowEmissionsPA", oneDairyCowEmissionsPA);
           intent.putExtra("TotalEmissionsPA", totalDairyEmissionsPA);
           startActivity(intent);

           Toast.makeText(DairyCalculationActivity.this, "Total Average Dairy Emissions Per Annum: " + totalDairyEmissionsPA, Toast.LENGTH_LONG);
       }
    }

    private boolean hasValidationErrors(String numDairyCows, String avgMilkYield) {
        if (numDairyCows.isEmpty()) {
            editTextNumDairyCows.setError("Number of Milk Producing Cows is Required");
            makeSnackBarMessage("Please insert Number of Milk Producing Cows.");
            return true;
        } else if (avgMilkYield.isEmpty()) {
            editTextAverageMilkYield.setError("Average Milk Yield Per Cow P.A. is Required");
            makeSnackBarMessage("Please insert Average Milk Yield Per Cow Per Annum.");
            return true;
        }else {
            return false;
        }
    }


    private void makeSnackBarMessage( String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
