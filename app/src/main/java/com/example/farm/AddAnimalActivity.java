package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddAnimalActivity extends AppCompatActivity implements View.OnClickListener {
    private final String  TAG= "AddAnimalActivity";
    private EditText tagNumber, animalName, dob, sex, dam, calvingDifficulty, sire, aiORstockbull, breed;
    private Button buttonSaveDetails;
    View mParentLayout;

    private static final String KEY_TAGNUMBER = "tagNumber";
    private static final String KEY_ANIMALNAME = "name";
    private static final String KEY_DOB= "dateOfBirth";
    private static final String KEY_SEX= "sex";
    private static final String KEY_DAM = "dam";
    private static final String KEY_SIRE = "sire";
    private static final String KEY_AiOrStockbull = "AI or Stock bull";
    private static final String KEY_CALVINGDIf = "calving difficulty";
    private static final String KEY_BREED = "breed";
    private static final String KEY_USERID = "user_id";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //vars
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);
        tagNumber = findViewById(R.id.editTextTagNumber);
        animalName = findViewById(R.id.editTextName);
        dam = findViewById(R.id.editTextDAM);
        dob = findViewById(R.id.editTextDob);
        sex = findViewById(R.id.editTextSex);
        calvingDifficulty = findViewById(R.id.editTextCalvingDifficulty);
        sire =findViewById(R.id.ediTextSire);
        breed =findViewById(R.id.editTextBreed);
        aiORstockbull =findViewById(R.id.editTextAIBull);


        mParentLayout = findViewById(android.R.id.content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonAddAnimal: {
                String strTag = tagNumber.getText().toString();
                String strName = animalName.getText().toString();
                String strDob = dob.getText().toString();
                String strSex = sex.getText().toString();
                String strDam = dam.getText().toString();
                String strCalvingDif = calvingDifficulty.getText().toString();
                String strSire = sire.getText().toString();
                String strBreed = breed.getText().toString();
                String strAiOrStockbull = aiORstockbull.getText().toString();
                addAnimal(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire);
                makeSnackBarMessage("Animal Inserted");
                //addAnimal(strTag, animalName, dob, sex, breed, dam, calvingDifficulty, aiORstockbull, sire);
            }
        }
    }

    public void insertAnimal(View view){
        buttonSaveDetails = findViewById(R.id.buttonAddAnimal);

        buttonSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTag = tagNumber.getText().toString();
                String strName = animalName.getText().toString();
                String strDob = dob.getText().toString();
                String strSex = sex.getText().toString();
                String strDam = dam.getText().toString();
                String strCalvingDif = calvingDifficulty.getText().toString();
                String strSire = sire.getText().toString();
                String strBreed = breed.getText().toString();
                String strAiOrStockbull = aiORstockbull.getText().toString();
                String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map<String, Object> animal = new HashMap<>();
                animal.put(KEY_TAGNUMBER, strTag);
                animal.put(KEY_ANIMALNAME, strName);
                animal.put(KEY_DOB, strDob);
                animal.put(KEY_SEX, strSex);
                animal.put(KEY_DAM, strDam);
                animal.put(KEY_CALVINGDIf, strCalvingDif);
                animal.put(KEY_SIRE, strSire);
                animal.put(KEY_BREED, strBreed);
                animal.put(KEY_AiOrStockbull, strAiOrStockbull);
                animal.put(KEY_USERID, strUserID);

                db.collection("Animals")
                        .add(animal)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Animal inserted into herd with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding animal into herd", e);
                            }
                        });

                //addAnimal(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire);
                Toast.makeText(AddAnimalActivity.this, "Animal inserted into Herd", Toast.LENGTH_SHORT).show();
               // startActivity(new Intent(MainActivity.this, AnimalActivity.class));

            }
        });
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    public void addAnimal(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire){

        DocumentReference addAnimalRef = db.collection("Animals").document();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Animal animal = new Animal(); //create new object
        animal.setTagNumber(tagNumber);
        animal.setAnimalName(animalName);
        animal.setDob(dob);
        animal.setSex(sex);
        animal.setDam(dam);
        animal.setBreed(breed);
        animal.setSire(sire);
        animal.setCalvingDifficulty(calvingDifficulty);
        animal.setAiORstockbull(aiORstockbull);
        animal.setUser_id(user_id);

        //inserts the data
        addAnimalRef.set(animal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Animal Added to Herd Successfully");
                }
                else{
                    makeSnackBarMessage("Failed to add Animal to herd.");
                }
            }
        });

    }
}
