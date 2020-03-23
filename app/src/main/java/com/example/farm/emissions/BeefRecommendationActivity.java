package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.farm.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class BeefRecommendationActivity extends AppCompatActivity {
    TextView totalBullEmissions,noOfBulls,totalCowEmissions,totalBeefEmissions,noOfCows;
    PieChart pieChart;
    int numberOfBullsAmount, numberOfCowsAmount;
    Double totalBeefEmissionsAmount, totalCowEmissionsAmount, totalBullEmissionAmount ;

//    PieDataSet dataset = new PieDataSet(entries, "");
//    PieData pieData = new PieData(dataset);



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

       int intBullEmissions = totalBullEmissionAmount.intValue();
        int intTotalBeefEmission = totalBeefEmissionsAmount.intValue();
        int bullEmissionsPercentage = (intBullEmissions * 100) / intTotalBeefEmission;
        int cowEmissionsPercentage =  (totalCowEmissionsAmount.intValue() * 100) / intTotalBeefEmission;

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(bullEmissionsPercentage, "Bulls"));
        value.add(new PieEntry(cowEmissionsPercentage, "Cows"));

        PieDataSet pieDataSet = new PieDataSet(value, " :Gender Values");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);

    }


}
