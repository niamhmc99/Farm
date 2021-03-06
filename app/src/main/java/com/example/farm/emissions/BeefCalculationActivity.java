package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class BeefCalculationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "BeefCalculationActivity";
    private int overallCowEmissions;
    private TextInputLayout textInputAverageCowWeight;
    private MaterialEditText editTextAverageCowWeight, editTextAverageBullWeight;
    private Button btnCalculate;
    private ArrayList<Integer> maleFemaleQuantity;
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private ConstraintLayout constraintLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);
        maleFemaleQuantity = new ArrayList<>();
        editTextAverageCowWeight = findViewById(R.id.editTextAverageCowWeight);
        editTextAverageBullWeight = findViewById(R.id.editTextAverageBullWeight);
        btnCalculate = findViewById(R.id.calculateBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_emissions);
        bottomNavigationView.setOnNavigationItemSelectedListener(BeefCalculationActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();
        constraintLayout = findViewById(R.id.constraintLayoutBeef);

        final Task<QuerySnapshot> maleQuery = db.collection("animals").whereEqualTo("user_id",currentUser).whereEqualTo("gender", "Male").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        maleFemaleQuantity.add(queryDocumentSnapshots.size());
                    }
                });

        maleQuery.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.toString());
                Toast.makeText(BeefCalculationActivity.this, "Error Occurred - Please try again", Toast.LENGTH_SHORT).show();
            }
        });


        final Task<QuerySnapshot> femaleQuery = db.collection("animals").whereEqualTo("user_id",currentUser).whereEqualTo("gender", "Female").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        maleFemaleQuantity.add(queryDocumentSnapshots.size());
                    }
                });



        femaleQuery.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.toString());
                Toast.makeText(BeefCalculationActivity.this, "Error Occurred - Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_home:
                Intent intent0 = new Intent(BeefCalculationActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(BeefCalculationActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(BeefCalculationActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(BeefCalculationActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(BeefCalculationActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }


    public void calculateBeefEmissions(View view) {

        String cowWeight= editTextAverageCowWeight.getText().toString();
        String bullWeight= editTextAverageBullWeight.getText().toString();

        if (maleFemaleQuantity.size() > 0 && !weightHasValidationError(cowWeight,bullWeight) ) {
            int numberOfBulls = maleFemaleQuantity.get(0);
            int numberOfCows = maleFemaleQuantity.get(1);

                double totalCowWeight = getTotalCowWeight(numberOfCows,cowWeight);
                double totalBullWeight = getTotalBullWeight(numberOfBulls,bullWeight);
                double totalCowEmissions = totalCowWeight * 25.43;
                double totalBullEmissions = totalBullWeight * 25.43;
                double totalBeefEmissions = totalBullEmissions + totalCowEmissions;

                Intent intent = new Intent(BeefCalculationActivity.this, BeefEmissionResultActivity.class);
                intent.putExtra("numberOfBulls", numberOfBulls);
                intent.putExtra("numberOfCows", numberOfCows);
                intent.putExtra("totalCowEmissions", totalCowEmissions);
                intent.putExtra("totalBullEmissions", totalBullEmissions);
                intent.putExtra("totalBeefEmissions", totalBeefEmissions);
                startActivity(intent);
            }
        else {
            Log.d("Quantity", "Male Female Number Retrieval Failed");
        }
    }

    private double getTotalCowWeight(int numberOfCows,String cowWeight) {

        if(!cowWeight.isEmpty()) {
            Double totalCowWeight = Double.parseDouble(cowWeight) * numberOfCows;
            return totalCowWeight;
        }
        return 0;
    }

    private double getTotalBullWeight(int numberOfBulls,String bullWeight) {

        if(!bullWeight.isEmpty()) {
            Double totalBullWeight = Double.parseDouble(bullWeight) * numberOfBulls;
            return totalBullWeight;
        }
        return 0;
    }


    private boolean weightHasValidationError(String cowWeight,String bullWeight){
        if(cowWeight.isEmpty() && bullWeight.isEmpty()){
            editTextAverageCowWeight.setError("Average Cow Weight is Required");
            editTextAverageBullWeight.setError("Average Bull Weight is Required");
            makeSnackBarMessage("Please enter data in at least one field");
            return true;
        }else {
            return false;
        }
    }


    private void makeSnackBarMessage( String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}