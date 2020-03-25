package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class BeefRecommendationActivity extends AppCompatActivity {
    TextView totalBullEmissions,noOfBulls,totalCowEmissions,totalBeefEmissions,noOfCows;
    PieChart pieChart;
    int numberOfBullsAmount, numberOfCowsAmount;
    Double totalBeefEmissionsAmount, totalCowEmissionsAmount, totalBullEmissionAmount ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_recommendation);
        pieChart = findViewById(R.id.pieChartBeef);
       // pieChart.setData(pieData);

        Intent intent = getIntent();
        numberOfBullsAmount = intent.getIntExtra("numberOfBulls",0);
        numberOfCowsAmount= intent.getIntExtra("numberOfCows",0);
        totalBeefEmissionsAmount = intent.getDoubleExtra("totalBeefEmissions",0);
        totalCowEmissionsAmount = intent.getDoubleExtra("totalCowEmissions",0);
        totalBullEmissionAmount = intent.getDoubleExtra("totalBullEmissions",0);

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

        setPieChartData();

    }

    public void setPieChartData(){

        Long longBullEmissions = Math.round(totalBullEmissionAmount);
        Long longCowEmissions = Math.round(totalBullEmissionAmount);
        int intTotalBeefEmission = totalBeefEmissionsAmount.intValue();
        int bullEmissionsPercentage = (Integer.valueOf(longBullEmissions.intValue()) * 100) / intTotalBeefEmission;
        int cowEmissionsPercentage =  (Integer.valueOf(longCowEmissions.intValue()) * 100) / intTotalBeefEmission;

        final List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(bullEmissionsPercentage, "Bulls"));
        value.add(new PieEntry(cowEmissionsPercentage, "Cows"));

        PieDataSet pieDataSet = new PieDataSet(value, " :Gender Values");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(10f);
        Description description = new Description();
        description.setText("Approx Bull: Cow Beef Emissions");
        pieChart.setDescription(description);
        pieChart.setUsePercentValues(true);
        pieChart.setTransparentCircleRadius(10f);
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

    }
}