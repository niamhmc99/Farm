package com.example.farm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class EmissionsActivity extends AppCompatActivity {

    ImageButton imageButtonBeef, imageButtonDairy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emissions);
    }

    public void clickBeef(View view){
        imageButtonBeef = findViewById(R.id.imageButtonBeef);
        imageButtonBeef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmissionsActivity.this, BeefCalculationActivity.class));

            }
        });

    }

    public void clickDairy(View view){
        imageButtonDairy = findViewById(R.id.imageButtonDairy);
        imageButtonDairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmissionsActivity.this, DairyCalculationActivity.class));

            }
        });
    }
}
