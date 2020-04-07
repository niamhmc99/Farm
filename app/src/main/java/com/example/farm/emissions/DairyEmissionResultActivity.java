package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.farm.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DairyEmissionResultActivity extends AppCompatActivity {


    LineChart linechart;
    Button display;
    TextView results;
    float score;
    int total = 0;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    ArrayList<Float> dataVals2 = new ArrayList<>();
    ArrayList<Entry> dataVals = new ArrayList<>();

    ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

TextView numCows, averageMilkYieldPA, totalDairyEmissionsPA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_emission_result);


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

        findViewById(R.id.waysToReduceDairy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DairyEmissionResultActivity.this, WaysToReduceEmissionsActivity.class);
                intent.putExtra("fileName", "DairyEmission.json");
                startActivity(intent);
            }
        });
    }

    public void display(){
        results = findViewById(R.id.results);


        dataVals.add(new Entry(0,dataVals2.get(0)));
        dataVals.add(new Entry(1,dataVals2.get(1)));
        dataVals.add(new Entry(2,dataVals2.get(2)));
        dataVals.add(new Entry(3,dataVals2.get(3)));
        /*dataVals.add(new Entry(0,12));
        dataVals.add(new Entry(1,10));
        dataVals.add(new Entry(2,9));
        dataVals.add(new Entry(3,11));*/

        LineDataSet lineDataSet1 = new LineDataSet(dataVals,"data set 1");
        lineDataSet1.setDrawCircles(true);
        lineDataSet1.setCircleColor(Color.RED);
        lineDataSet1.setCircleHoleRadius(6);
        lineDataSet1.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet1);


        linechart.setData(new LineData(lineDataSets));
        linechart.setVisibleXRangeMaximum(65f);
        linechart.invalidate();

        float difference = dataVals2.get(3)- dataVals2.get(0);
        float differencepositive = dataVals2.get(0)- dataVals2.get(3);
        float total = dataVals2.get(0) + dataVals2.get(1) + dataVals2.get(2) + dataVals2.get(3);
        float average = total / 4;
        float nationalaverage =14;
        float posave = nationalaverage - average;



    }
}
