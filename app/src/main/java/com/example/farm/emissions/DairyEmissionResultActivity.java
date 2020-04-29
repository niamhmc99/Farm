package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.farm.R;
import java.text.DecimalFormat;

public class DairyEmissionResultActivity extends AppCompatActivity {
TextView numCows, averageMilkYieldPA, totalDairyEmissionsPA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_emission_result);

        Intent intent = getIntent();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        int numberOfCows = intent.getIntExtra("NumberDairyCows",0);
        int averageMilkYield= intent.getIntExtra("AverageMilkYield",0);
        double totalEmissionsPA= intent.getDoubleExtra("TotalEmissionsPA",0);

        totalDairyEmissionsPA = findViewById(R.id.totalDairyEmissionsPA);
        totalDairyEmissionsPA.setText(decimalFormat.format(totalEmissionsPA));

        numCows = findViewById(R.id.numCows);
        numCows.setText(String.valueOf(numberOfCows));

        averageMilkYieldPA= findViewById(R.id.averageMilkYieldPA);
        averageMilkYieldPA.setText(String.valueOf(averageMilkYield));

        findViewById(R.id.waysToReduceDairy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DairyEmissionResultActivity.this, WaysToReduceEmissionsActivity.class);
                intent.putExtra("fileName", "DairyEmission.json");
                startActivity(intent);
            }
        });
    }
}
