package com.example.farm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.invoiceExpenses.ExpenditureActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    ImageButton imageButtonAnimals, imageButtonVets, buttonLogout, imageButtonWeather, imageButtonMap, imageButtonToDo, imageButtonEmissions;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private DrawerLayout drawerLayout;


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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer(toolbar);


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

    private void drawer(Toolbar toolbar) {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        getUserName(navigationView);
    }


    private void getUserName(NavigationView navigationView) {

        View headerLayout = navigationView.getHeaderView(0);
        TextView username = headerLayout.findViewById(R.id.farmersEmail);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username.setText(currentUser.getEmail());
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


    public void clickVets(View view) {
        imageButtonVets = findViewById(R.id.imageButtonVets);
        imageButtonVets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VetActivity.class));

            }
        });
    }


    public void clickWeather(View view) {
        imageButtonWeather = findViewById(R.id.imageButtonWeather);
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

    public void clickToDoList(View view) {
        imageButtonToDo = findViewById(R.id.imageButtonToDoList);
        imageButtonToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ToDoListActivity.class));
            }
        });
    }

    public void clickGoogleMap(View view) {
        imageButtonMap = findViewById(R.id.imageButtonGoogleMap);
        imageButtonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    public void clickEmissions(View view) {
        imageButtonEmissions = findViewById(R.id.imageButtonEmissions);
        imageButtonEmissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmissionsActivity.class));
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumainopts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                closeDrawer();
                break;
            case R.id.nav_animal:
                startActivity(new Intent(MainActivity.this, AnimalActivity.class));
                break;
            case R.id.nav_weather:
                startActivity(new Intent(MainActivity.this, WeatherActivity2.class));
                break;
            case R.id.nav_nearbyPlaces:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                break;
            case R.id.nav_vets:
                startActivity(new Intent(MainActivity.this, VetActivity.class));
                break;
            case R.id.nav_expenses:
                startActivity(new Intent(MainActivity.this, ExpenditureActivity.class));
                break;
        }
        return true;
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}