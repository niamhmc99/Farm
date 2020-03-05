package com.example.farm.emissions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class BeefCalculationActivity extends AppCompatActivity {

    FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final String  TAG= "BeefCalculationActivity";
    private int overallCowEmissions;
    private TextInputLayout textInputAverageCowWeight;

    //EditText variables
    private EditText editTextAverageCowWeight, editTextAverageBullWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);

//      textInputAverageCowWeight = findViewById(R.id.textInputAverageCowWeight);
        editTextAverageCowWeight = findViewById(R.id.editTextAverageCowWeight);
        editTextAverageBullWeight = findViewById(R.id.editTextAverageBullWeight);


    }

    public void calculateBeefEmissions(View view) {

        Button calculateBtn = findViewById(R.id.calculateBtn);
        String bullWeight = editTextAverageBullWeight.getText().toString();
        String cowWeight = editTextAverageCowWeight.getText().toString();
        calculateCowEmissions();
        calculateCowNumber();
    }


    public void calculateCowEmissions(){
        db.collection("animals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int numberFemaleAnimals = task.getResult().size();
                            double totalCowWeight = getTotalCowWeight(numberFemaleAnimals);


                            Intent intent = new Intent(BeefCalculationActivity.this, EmissionsActivity.class);
                            intent.putExtra("totalCowWeight", totalCowWeight);

                            startActivity(intent);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });

    }

    public void calculateCowNumber(){
        db.collection("animals").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots != null){
                    int numberOfCows = queryDocumentSnapshots.size();
                    getTotalCowWeight(numberOfCows);
                    System.out.println(numberOfCows + " *************Number of animals in DATAbaseee");
                }
            }

        });


    }

    public double getTotalCowWeight(int numberOfCows)
    {

        String cowWeight = editTextAverageCowWeight.getText().toString();
        Double beefAnimalEmissions = Double.parseDouble(cowWeight)* numberOfCows * 25.43;
        System.out.println(beefAnimalEmissions + " ******* cowWeight X numberOFCows" );

        Intent intent = new Intent(BeefCalculationActivity.this, EmissionsActivity.class);
        intent.putExtra("totalCowWeight", beefAnimalEmissions);
        startActivity(intent);

        return  beefAnimalEmissions;

    }

    //*** if add cow average + bull average

//    public void calculateCowEmissions(){
//        db.collection("animals")
//                .whereEqualTo("gender", "female")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//
//                                 int numberCows = task.getResult().size();
//                                 double totalCowWeight= getTotalCowWeight(numberCows);
//
//                               Intent intent = new Intent(BeefCalculationActivity.this, BeefRecommendationsActivity.class);
//                               intent.putExtra("totalCowWeight", totalCowWeight);
//
//                               startActivity(intent);
//                         } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//
//                });
//
//    }





}



//    public Task<Integer> getCount(final DocumentReference ref) {
//        // Sum the count of each shard in the subcollection
//        return ref.collection("shards").get()
//                .continueWith(new Continuation<QuerySnapshot, Integer>() {
//                    @Override
//                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
//                        int count = 0;
//                        for (DocumentSnapshot snap : task.getResult()) {
//                            Shard shard = snap.toObject(Shard.class);
//                            count += shard.count;
//                        }
//                        return count;
//                    }
//                });
//    }

//
//    public void userGenderCheck() {
//        userColRef.document(user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Map animalInfo = task.getResult().getData();
//                            if (userInfo.get("gender") != null) {
//                                int gendercheck = Integer.parseInt(animalInfo.get("gender").toString());
//
//                                textViewSetting();
//
//                                switch (gendercheck) {
//                                    case 0:
//                                        womanbutton.setChecked(true);
//                                        break;
//                                    case 1:
//                                        manbutton.setChecked(true);
//                                        break;
//                                }
//                            } else {
//                                textViewSetting();
//                            }
//                        } else {
//                            Log.d(TAG, "get failed with ", task.getException());
//                        }
//                    }
//                });
//    }


//    public Task<Integer> getCount(final DocumentReference ref) {
//        // Sum the count of each shard in the subcollection
//        return ref.collection("shards").get()
//                .continueWith(new Continuation<QuerySnapshot, Integer>() {
//                    @Override
//                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
//                        int count = 0;
//                        for (DocumentSnapshot snap : task.getResult()) {
//                            Shard shard = snap.toObject(Shard.class);
//                            count += shard.count;
//                        }
//                        return count;
//                    }
//                });
//    }
//