package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.farm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class BeefCalculationActivity extends AppCompatActivity {

    FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final String  TAG= "BeefCalculationActivity";

    private TextInputLayout textInputAverageCowWeight;

    //EditText variables
    private EditText editTextAverageCowWeight, editTextAverageBullWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);

//        textInputAverageCowWeight = findViewById(R.id.textInputAverageCowWeight);
        editTextAverageCowWeight = findViewById(R.id.editTextAverageCowWeight);
        editTextAverageBullWeight = findViewById(R.id.editTextAverageBullWeight);


    }

    public void calculateBeefEmissions(View view) {

        Button calculateBtn = findViewById(R.id.calculateBtn);
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCowEmissions();
            }
        });


        String bullWeight = editTextAverageBullWeight.getText().toString();
        calculateCowEmissions();
    }

    public void calculateCowEmissions(){
        db.collection("animals")
                .whereEqualTo("gender", "female")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String cowWeight = editTextAverageCowWeight.getText().toString();
                                int numberCows = task.getResult().size();
                                Double totalCowEmissions = Double.parseDouble(cowWeight) * numberCows;
//                                Intent intent = new Intent(BeefCalculationActivity.this, BeefRecommendationsActivity.class);
//                                intent.putExtra("beefCow", totalCowEmissions);
//                                startActivity(intent);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
