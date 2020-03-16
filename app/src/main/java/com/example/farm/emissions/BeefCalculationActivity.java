package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.UpdateAnimalActivity;
import com.example.farm.VetActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class BeefCalculationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "BeefCalculationActivity";
    private int overallCowEmissions;
    private TextInputLayout textInputAverageCowWeight;
    private MaterialEditText editTextAverageCowWeight, editTextAverageBullWeight;
    private Button btnCalculate;
    BottomNavigationView bottomNavigationView;
    ArrayList animalMaleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);

        editTextAverageCowWeight = findViewById(R.id.editTextAverageCowWeight);
        editTextAverageBullWeight = findViewById(R.id.editTextAverageBullWeight);
        btnCalculate = findViewById(R.id.calculateBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(BeefCalculationActivity.this);
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
        ArrayList<Integer> maleFemaleAmounts = retrieveGenderQuantities();

        if (maleFemaleAmounts.size() >= 0) {
            int numberOfCows = maleFemaleAmounts.indexOf(1);
            int numberOfBulls = maleFemaleAmounts.indexOf(0);
            double totalCowWeight = getTotalCowWeight(numberOfCows);
            double totalBullWeight = getTotalBullWeight(numberOfBulls);
            double totalCowEmissions = totalCowWeight * 25.43;
            double totalBullEmissions = totalBullWeight * 25.43;
            Intent intent = new Intent(BeefCalculationActivity.this, BeefRecommendationActivity.class);
            intent.putExtra("numberOfBulls", numberOfBulls);
            intent.putExtra("numberOfCows", numberOfCows);
            intent.putExtra("totalCowWeight", totalCowWeight);
            intent.putExtra("totalBullWeight", totalBullWeight);
            intent.putExtra("totalCowEmissions", totalCowEmissions);
            intent.putExtra("totalBullEmissions", totalBullEmissions);
            startActivity(intent);
        } else {
            Log.d("Quantity", "Male Female number retrieval failed");
        }    }

    public ArrayList<Integer> retrieveGenderQuantities() {


        animalMaleList = new ArrayList<>();
        db.collection("animals").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //convert d to our animal object
                                Animal a = d.toObject(Animal.class);
                                animalMaleList.add(a);
                            }
                        }
                    }
                });

        System.out.println("********************" + animalMaleList.size());


        final ArrayList<Integer> maleFemaleQuantity = new ArrayList<>();

         Task<QuerySnapshot> maleQuery = db.collection("animals").get()
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


         Task<QuerySnapshot> femaleQuery = db.collection("animals").get()
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

        System.out.println("Array size male and female quantity: " + maleFemaleQuantity.size());
        return maleFemaleQuantity;
    }


    public double getTotalCowWeight(int numberOfCows) {
        String cowWeight = editTextAverageCowWeight.getText().toString();
        Double totalCowWeight = Double.parseDouble(cowWeight) * numberOfCows;
        return totalCowWeight;
    }

    public double getTotalBullWeight(int numberOfBulls) {
        String bullWeight = editTextAverageBullWeight.getText().toString();
        Double totalBullWeight = Double.parseDouble(bullWeight) * numberOfBulls;
        return totalBullWeight;
    }

}