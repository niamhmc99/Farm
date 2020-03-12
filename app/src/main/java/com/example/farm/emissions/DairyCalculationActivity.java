package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.farm.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DairyCalculationActivity extends AppCompatActivity {

    private MaterialEditText editTextNumDairyCows;
    private MaterialEditText editTextAverageMilkYield;
//    Button btnCalculate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_calculation);
    editTextNumDairyCows = findViewById(R.id.editTextNumDairyCows);
    editTextAverageMilkYield = findViewById(R.id.editTextAverageMilkYield);

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
        intent.putExtra("AverageMilkYield", avgMilkYield);
        intent.putExtra("OneCowEmissionsPA", oneDairyCowEmissionsPA);
        intent.putExtra("TotalEmissionsPA", totalDairyEmissionsPA);
        startActivity(intent);

        System.out.println("One Dairy Emissions ***" + oneDairyCowEmissionsPA);
        System.out.println("Total Dairy Emissions ***" + totalDairyEmissionsPA);


        Toast.makeText(DairyCalculationActivity.this, "Total Dairy Emissions Per Annumn: " + totalDairyEmissionsPA, Toast.LENGTH_LONG);

    }
}
