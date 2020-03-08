package com.example.farm.emissions;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BeefCalculationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "BeefCalculationActivity";
    private int overallCowEmissions;
    private TextInputLayout textInputAverageCowWeight;

    //EditText variables
    private EditText editTextAverageCowWeight, editTextAverageBullWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beef_calculation);

        editTextAverageCowWeight = findViewById(R.id.editTextAverageCowWeight);
        editTextAverageBullWeight = findViewById(R.id.editTextAverageBullWeight);
    }

    public void calculateBeefEmissions(View view) {
        calculateAnimalEmissions();
    }


    public void calculateAnimalEmissions() {

        ArrayList<Integer> maleFemaleAmounts= retrieveGenderQuantities();

        if(maleFemaleAmounts.size()>0){

            int numberOfBulls= maleFemaleAmounts.get(0);
            int numberOfCows= maleFemaleAmounts.get(1);


            double totalCowWeight= getTotalCowWeight(numberOfCows);
            double totalBullWeight= getTotalBullWeight(numberOfBulls);

            double totalCowEmissions = totalCowWeight* 25.43;
            double totalBullEmissions = totalBullWeight * 25.43;


            Intent intent = new Intent(BeefCalculationActivity.this, BeefRecommendationActivity.class);
            intent.putExtra("numberOfBulls", numberOfBulls);
            intent.putExtra("numberOfCows",numberOfCows);
            intent.putExtra("totalCowWeight", totalCowWeight);
            intent.putExtra("totalBullWeight", totalBullWeight);
            intent.putExtra("totalCowEmissions",totalCowEmissions);
            intent.putExtra("totalBullEmissions",totalBullEmissions);


            startActivity(intent);

        }

        else{
            Log.d("Quantity","Male Female number retrieval failed");
        }


    }

    public ArrayList<Integer> retrieveGenderQuantities(){

    Query maleQuery = db.collection("animals").whereEqualTo("gender","male");
    Query femaleQuery = db.collection("animals").whereEqualTo("gender","female");
    Task maleTask = maleQuery.get();
    Task femaleTask = femaleQuery.get();
    final ArrayList<Integer>maleFemaleQuantity= new ArrayList<>();


        Task<List<QuerySnapshot>> combinedTask = Tasks.whenAllSuccess(maleTask, femaleTask);

        combinedTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
         @Override
         public void onSuccess(List<QuerySnapshot> list) {

             maleFemaleQuantity.add(list.get(0).size());
             maleFemaleQuantity.add(list.get(1).size());
          }

     });

        combinedTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BeefCalculationActivity.this, "Error Occurred - Please try again", Toast.LENGTH_SHORT).show();
            }

        });

        return maleFemaleQuantity;

    }


    public double getTotalCowWeight(int numberOfCows)
    {
        String cowWeight = editTextAverageCowWeight.getText().toString();
        Double totalCowWeight = Double.parseDouble(cowWeight)* numberOfCows;
        return  totalCowWeight;
    }

    public double getTotalBullWeight(int numberOfBulls){
        String bullWeight = editTextAverageBullWeight.getText().toString();
        Double totalBullWeight = Double.parseDouble(bullWeight)* numberOfBulls;
        return  totalBullWeight;
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