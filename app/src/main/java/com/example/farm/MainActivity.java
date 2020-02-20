package com.example.farm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {


    ImageButton imageButtonAnimals, imageButtonVets, buttonLogout, imageButtonWeather, imageButtonMap, imageButtonToDo;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        buttonLogout = findViewById(R.id.imageButtonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        super.onResume();
    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthStateListener);
    }

    public void onStop() {
        super.onStop();
        auth.addAuthStateListener(mAuthStateListener);
    }

    public void clickAnimals(View view) {
        imageButtonAnimals = findViewById(R.id.imageButtonAnimals);
        imageButtonAnimals.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      Toast.makeText(MainActivity.this, "Animals Registered", Toast.LENGTH_SHORT).show();
                                                      startActivity(new Intent(MainActivity.this, AnimalActivity.class));

                                                  }
                                              }
        );
    }



    public void clickVets (View view){
        imageButtonVets = findViewById(R.id.imageButtonVets);
        imageButtonVets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VetActivity.class));

            }
        });
    }


    public void clickWeather(View view){
        imageButtonWeather =findViewById(R.id.imageButtonWeather);
        imageButtonWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WeatherActivity2.class));
            }
        });

    }

//    public void clickWeatherDetz(View view){
//        Button buttonWeather =findViewById(R.id.buttonWeather);
//        buttonWeather.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
//            }
//        });
//
//    }

    public void clickToDoList(View view){
        imageButtonToDo = findViewById(R.id.imageButtonToDoList);
        imageButtonToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ToDoListActivity.class));
            }
        });
    }

    public void clickGoogleMap(View view){
        imageButtonMap = findViewById(R.id.imageButtonGoogleMap);
        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumainopts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUsers() {
//        DocumentReference docRef = db.collection("users").document("SF");
//// asynchronously retrieve the document
//       // ApiFuture<DocumentSnapshot> future = docRef.get();
//// ...
//// future.get() blocks on response
//       // DocumentSnapshot document = future.get();
//        if (document.exists()) {
//            System.out.println("Document data: " + document.getData());
//        } else {
//            System.out.println("No such document!");
//        }
    }
}