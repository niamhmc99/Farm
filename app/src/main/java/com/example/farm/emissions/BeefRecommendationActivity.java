package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.farm.R;

public class BeefRecommendationActivity extends AppCompatActivity {
    TextView totalBullEmissions,noOfBulls,totalCowEmissions,totalBeefEmissions,noOfCows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_recommendation);

        Intent intent = getIntent();
        int numberOfBullsAmount = intent.getIntExtra("numberOfBulls",0);
        int numberOfCowsAmount= intent.getIntExtra("numberOfCows",0);
        Double totalBeefEmissionsAmount = intent.getDoubleExtra("totalBeefEmissions",0);
        Double totalCowEmissionsAmount = intent.getDoubleExtra("totalCowEmissions",0);
        Double totalBullEmissionAmount = intent.getDoubleExtra("totalBullEmissions",0);

        totalBullEmissions = findViewById(R.id.totalBullEmissions);
        totalBullEmissions.setText(totalBullEmissionAmount.toString());

        totalCowEmissions = findViewById(R.id.totalCowEmissions);
        totalCowEmissions.setText(totalCowEmissionsAmount.toString());

        noOfBulls = findViewById(R.id.noBulls);
        noOfBulls.setText(String.valueOf(numberOfBullsAmount));

        noOfCows= findViewById(R.id.noCows);
        noOfCows.setText(String.valueOf(numberOfCowsAmount));

        totalBeefEmissions = findViewById(R.id.totalBeefEmissions);
        totalBeefEmissions.setText(totalBeefEmissionsAmount.toString());

    }
}
