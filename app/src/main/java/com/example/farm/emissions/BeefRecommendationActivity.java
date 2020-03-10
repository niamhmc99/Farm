package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.farm.R;

public class BeefRecommendationActivity extends AppCompatActivity {
    TextView totalBullEmissions,noOfBulls,totalCowEmissions,noOfCows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_recommendation);

        Intent intent = getIntent();
        Double totalBeefEmissions = intent.getDoubleExtra("totalCowWeight",0);
        int numberOfBullsAmount = intent.getIntExtra("numberOfBulls",0);
        int numberOfCowsAmount= intent.getIntExtra("numberOfCows",0);
        Double totalCowWeightAmount = intent.getDoubleExtra("totalCowWeight",0);
        Double totalBullWeightAmount = intent.getDoubleExtra("totalBullWeight",0);
        Double totalCowEmissionsAmount = intent.getDoubleExtra("totalCowEmissions",0);
        Double totalBullEmissionAmount = intent.getDoubleExtra("totalBullEmissions",0);

        totalBullEmissions = findViewById(R.id.totalBullEmissions);
        totalBullEmissions.setText(totalBullEmissionAmount.toString());

        noOfBulls = findViewById(R.id.noBulls);
        noOfBulls.setText(numberOfBullsAmount);

        noOfCows= findViewById(R.id.noCows);
        noOfCows.setText(numberOfCowsAmount);
    }
}
