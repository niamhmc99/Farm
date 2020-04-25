package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.farm.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BeefEmissionResultActivity extends AppCompatActivity {
    TextView totalBullEmissions,noOfBulls,totalCowEmissions,totalBeefEmissions,noOfCows;
    PieChart pieChart;
    int numberOfBullsAmount, numberOfCowsAmount;
    Double totalBeefEmissionsAmount, totalCowEmissionsAmount, totalBullEmissionAmount ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_emission_result);
        pieChart = findViewById(R.id.pieChartBeef);

        Intent intent = getIntent();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        numberOfBullsAmount = intent.getIntExtra("numberOfBulls",0);
        numberOfCowsAmount= intent.getIntExtra("numberOfCows",0);
        totalBeefEmissionsAmount = intent.getDoubleExtra("totalBeefEmissions",0);
        totalCowEmissionsAmount = intent.getDoubleExtra("totalCowEmissions",0);
        totalBullEmissionAmount = intent.getDoubleExtra("totalBullEmissions",0);

        totalBullEmissions = findViewById(R.id.totalBullEmissions);
        totalBullEmissions.setText(decimalFormat.format(totalBullEmissionAmount));

        totalCowEmissions = findViewById(R.id.totalCowEmissions);
        totalCowEmissions.setText(decimalFormat.format(totalCowEmissionsAmount));

        noOfBulls = findViewById(R.id.noBulls);
        noOfBulls.setText(String.valueOf(numberOfBullsAmount));

        noOfCows= findViewById(R.id.noCows);
        noOfCows.setText(String.valueOf(numberOfCowsAmount));

        totalBeefEmissions = findViewById(R.id.totalBeefEmissions);
        totalBeefEmissions.setText(decimalFormat.format(totalBeefEmissionsAmount));

        if (totalBeefEmissionsAmount>0) {
            setPieChartData();
        }

        findViewById(R.id.waysToReduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BeefEmissionResultActivity.this, WaysToReduceEmissionsActivity.class);
                intent.putExtra("fileName", "BeefEmission.json");
                startActivity(intent);
            }
        });

    }

    public void setPieChartData(){

        Long longBullEmissions = Math.round(totalBullEmissionAmount);
        Long longCowEmissions = Math.round(totalCowEmissionsAmount);
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