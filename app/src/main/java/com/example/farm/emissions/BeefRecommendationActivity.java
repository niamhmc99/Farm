package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.farm.R;

public class BeefRecommendationActivity extends AppCompatActivity {
TextView totalEmissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_recommendation);

        Intent intent = getIntent();
        Double totalBeefEmissions = intent.getDoubleExtra("totalCowWeight",0);
        totalEmissions = findViewById(R.id.textViewTotalEmissions);
        totalEmissions.setText(totalBeefEmissions.toString());
    }
}
