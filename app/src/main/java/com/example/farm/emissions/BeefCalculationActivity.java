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
    private ArrayList<Integer> maleFemaleQuantity = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private String currentUser;
    private ConstraintLayout constraintLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);

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


        final Task<QuerySnapshot> femaleQuery = db.collection("animals").whereEqualTo("gender", "Female").get()
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
        if (maleFemaleQuantity.size() > 0) {
            int numberOfCows = maleFemaleQuantity.get(1);
            int numberOfBulls = maleFemaleQuantity.get(0);

            double totalCowWeight = getTotalCowWeight(numberOfCows);
            double totalBullWeight = getTotalBullWeight(numberOfBulls);
            double totalCowEmissions = totalCowWeight * 25.43;
            double totalBullEmissions = totalBullWeight * 25.43;
            double totalBeefEmissions = totalBullEmissions + totalCowEmissions;

            Intent intent = new Intent(BeefCalculationActivity.this, BeefEmissionResultActivity.class);
            intent.putExtra("numberOfBulls", numberOfBulls);
            intent.putExtra("numberOfCows", numberOfCows);
            intent.putExtra("totalCowEmissions", totalCowEmissions);
            intent.putExtra("totalBullEmissions", totalBullEmissions);
            intent.putExtra("totalBeefEmissions",totalBeefEmissions);
            startActivity(intent);
        } else {
            Log.d("Quantity", "Male Female Number Retrieval Failed");
        }
    }





    public double getTotalCowWeight(int numberOfCows) {
        String cowWeight = editTextAverageCowWeight.getText().toString();
        if (!cowWeightHasValidationError(cowWeight)) {
            Double totalCowWeight = Double.parseDouble(cowWeight) * numberOfCows;
            return totalCowWeight;
        }
        return 0;
    }

//        if(cowWeight.isEmpty()){
//            editTextAverageCowWeight.setError("Average Cow Weight is Required");
//            makeSnackBarMessage("Please insert the Average Weight of your Cows.");
//        }
//        if (cowWeight != null && cowWeight.length() > 0) {
//            try {
//                Double totalCowWeight = Double.parseDouble(cowWeight) * numberOfCows;
//                return totalCowWeight;
//            } catch(Exception e) {
//                return -1;
//                        //makeSnackBarMessage("Please insert the Average Weight of your Cows.");   // or some value to mark this field is wrong. or make a function validates field first ...
//            }
//        }
//        else return 0;

    private boolean cowWeightHasValidationError(String cowWeight){
        if(cowWeight.isEmpty()){
            editTextAverageCowWeight.setError("Average Cow Weight is Required");
            makeSnackBarMessage("Please insert the Average Weight of your Cows.");
            return true;
        }else {
            return false;
        }
    }

    private boolean bullWeightHasValidationError(String bullWeight){
        if(bullWeight.isEmpty()){
            editTextAverageBullWeight.setError("Average Bull Weight is Required");
            makeSnackBarMessage("Please insert the Average Weight of your Bulls.");
            return true;
        }else{
            return false;
        }
    }

    public double getTotalBullWeight(int numberOfBulls) {
        String bullWeight = editTextAverageBullWeight.getText().toString();
        if (!bullWeightHasValidationError(bullWeight)) {
            Double totalBullWeight = Double.parseDouble(bullWeight) * numberOfBulls;
            return totalBullWeight;
        }
        return 0;
    }

//        if (bullWeight != null && bullWeight.length() > 0) {
//            try {
//                Double totalBullWeight = Double.parseDouble(bullWeight) * numberOfBulls;
//                return totalBullWeight;
//            } catch(Exception e) {
//                return -1;
//                //makeSnackBarMessage("Please insert the Average Weight of your Cows.");   // or some value to mark this field is wrong. or make a function validates field first ...
//            }
//        }
//        if(bullWeight.isEmpty()){
//            editTextAverageBullWeight.setError("Average Bull Weight is Required");
//            makeSnackBarMessage("Please insert the Average Weight of your Bulls.");
//        }
//        return totalBullWeight;


    private void makeSnackBarMessage( String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}