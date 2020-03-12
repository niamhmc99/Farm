package com.example.farm.emissions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.farm.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DairyCalculationActivity extends AppCompatActivity {

    private MaterialEditText editTextNumDairyCows, editTextAverageMilkYield;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy_calculation);
editTextNumDairyCows = findViewById(R.id.editTextNumDairyCows);
editTextAverageMilkYield = findViewById(R.id.editTextAverageMilkYield);
    }

    public void calculateDairyEmissions(View view){


    }
}
