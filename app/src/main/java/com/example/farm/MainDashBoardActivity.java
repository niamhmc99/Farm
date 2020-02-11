package com.example.farm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainDashBoardActivity extends AppCompatActivity {

    ImageButton imageButtonAnimal, imageButtonWeather, imageButtonToDo, imageButtonLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dash_board);

        imageButtonLogout = findViewById(R.id.imageButtonLogout);
    }

    public void clickAnimals(View view){
        imageButtonAnimal = findViewById(R.id.imageButtonAnimals);

    }

    public void clickWeather(View view){
        imageButtonWeather = findViewById(R.id.imageButtonWeather);
    }

    public void clickMaps(View view){
        imageButtonToDo = findViewById(R.id.imageButtonToDoList);
    }
}
