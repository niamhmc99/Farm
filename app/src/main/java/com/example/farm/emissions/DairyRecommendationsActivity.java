package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.farm.R;

public class DairyRecommendationsActivity extends AppCompatActivity {

TextView numCows, averageMilkYieldPA, totalDairyEmissionsPA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_recommendations);


        Intent intent = getIntent();


        int numberOfCows = intent.getIntExtra("NumberDairyCows",0);
        int averageMilkYield= intent.getIntExtra("AverageMilkYield",0);
        double oneCowEmissionsPA = intent.getDoubleExtra("OneCowEmissionsPA",0);
        double totalEmissionsPA= intent.getDoubleExtra("TotalEmissionsPA",0);

        totalDairyEmissionsPA = findViewById(R.id.totalDairyEmissionsPA);
        totalDairyEmissionsPA.setText(String.valueOf(totalEmissionsPA));

        numCows = findViewById(R.id.numCows);
        numCows.setText(String.valueOf(numberOfCows));

        averageMilkYieldPA= findViewById(R.id.averageMilkYieldPA);
        averageMilkYieldPA.setText(String.valueOf(averageMilkYield));



    }
}
