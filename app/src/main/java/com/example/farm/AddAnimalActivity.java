package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.javaClasses.AnimalAdapter;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAnimalActivity extends AppCompatActivity {
    private final String  TAG= "AddAnimalActivity";
    private EditText editTextTagNumber, editTextAnimalName, editTextDob, editTextSex, editTextDam, editTextCalvingDifficulty, editTextsire, editTextAiORstockbull, editTextBreed;
    private Button buttonSaveDetails;
    View mParentLayout;
private List<Animal>animalList;
private AnimalAdapter adapter;
private Animal animal;

    private static final String KEY_TAGNUMBER = "tagNumber";
    private static final String KEY_ANIMALNAME = "animalName";
    private static final String KEY_DOB= "dob";
    private static final String KEY_SEX= "sex";
    private static final String KEY_DAM = "dam";
    private static final String KEY_SIRE = "sire";
    private static final String KEY_AiOrStockbull = "aiORstockbull";
    private static final String KEY_CALVINGDIf = "calvingDifficulty";
    private static final String KEY_BREED = "breed";
    private static final String KEY_USERID = "user_id";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //vars
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);
        editTextTagNumber = findViewById(R.id.editTextTagNumber);
        editTextAnimalName = findViewById(R.id.editTextName);
        editTextDam = findViewById(R.id.editTextDAM);
        editTextDob = findViewById(R.id.editTextDob);
        editTextSex = findViewById(R.id.editTextSex);
        editTextCalvingDifficulty = findViewById(R.id.editTextCalvingDifficulty);
        editTextsire =findViewById(R.id.ediTextSire);
        editTextBreed =findViewById(R.id.editTextBreed);
        editTextAiORstockbull =findViewById(R.id.editTextAIBull);

animalList = new ArrayList<>();

        mParentLayout = findViewById(android.R.id.content);

        db.collection("animals").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //progressBar.setVisibility(View.GONE);

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {

                                Animal a = d.toObject(Animal.class);
                                //p.setId(d.getId());
                                animalList.add(a);

                            }

                            //adapter.notifyDataSetChanged();

                        }


                    }
                });

    }


    public void insertAnimal(View view){
        buttonSaveDetails = findViewById(R.id.buttonAddAnimal);

        buttonSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTag = editTextTagNumber.getText().toString().trim();
                String strName = editTextAnimalName.getText().toString().trim();
                String strDob = editTextDob.getText().toString().trim();
                String strSex = editTextSex.getText().toString().trim();
                String strDam = editTextDam.getText().toString().trim();
                String strCalvingDif = editTextCalvingDifficulty.getText().toString().trim();
                String strSire = editTextsire.getText().toString().trim();
                String strBreed = editTextBreed.getText().toString().trim();
                String strAiOrStockbull = editTextAiORstockbull.getText().toString().trim();
                String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();


                final Map<String, Object> animal = new HashMap<>();
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

                if(!hasValidationErrors(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire) == true) {
                    db.collection("Animals")
                            .document("animalID")
                            .set(animal);
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Log.d(TAG, "Animal inserted into herd with ID: " + documentReference.getId());
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error adding animal into herd", e);
//                                }
//                            });

                    //addAnimal(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire);
                    Toast.makeText(AddAnimalActivity.this, "Animal inserted into Herd", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MainActivity.this, AnimalActivity.class));


                }
            }
        });
    }
//if(!hasValidationErrors(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire) == true) {
//        db.collection("Animals")
//                .add(animal)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "Animal inserted into herd with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding animal into herd", e);
//                    }
//                });
//
//        //addAnimal(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire);
//        Toast.makeText(AddAnimalActivity.this, "Animal inserted into Herd", Toast.LENGTH_SHORT).show();
//        // startActivity(new Intent(MainActivity.this, AnimalActivity.class));
//
//
//    }
    private boolean hasValidationErrors(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire){
        if (tagNumber.trim().isEmpty()) {
            editTextTagNumber.setError("TagNumber is required");
            makeSnackBarMessage("Please insert Tag Number.");
            return true;
        } else if (animalName.isEmpty()) {
            editTextAnimalName.setError("Animal Name Required");
            makeSnackBarMessage("Please insert Animal Name.");
            return true;
        }else if(dob.isEmpty()){
            editTextDob.setError("Date of Birth is required.");
            makeSnackBarMessage("Please insert Date of Birth.");
            return true;
        }else if(sex.isEmpty()){
            editTextSex.setError("The sex of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Sex.");
            return true;
        }else if(dam.isEmpty()){
            editTextDam.setError("The Dam of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Dam.");
            return true;
        }else if(calvingDifficulty.isEmpty()){
            editTextCalvingDifficulty.setError("Calving Difficulty is required");
            makeSnackBarMessage("Please insert the Calving Difficulty.");
            return true;
        }else if(sire.isEmpty()){
            editTextsire.setError("The Sire of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Sire.");
            return true;
        }else if(aiORstockbull.isEmpty()){
            editTextAiORstockbull.setError("AI / Stock Bull?");
            makeSnackBarMessage("Please insert if insemination was by AI or Stock bull.");
            return true;
        }else if(breed.isEmpty()){
            editTextBreed.setError("Breed of Animal is Required.s");
            makeSnackBarMessage("Please insert Animal Breed.");
            return true;
        }else{
            return false;
        }

    }
    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

//    public void addAnimal(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire){
//
//        DocumentReference addAnimalRef = db.collection("Animals").document();
//
//        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Animal animal = new Animal(); //create new object
//        animal.setTagNumber(tagNumber);
//        animal.setAnimalName(animalName);
//        animal.setDob(dob);
//        animal.setSex(sex);
//        animal.setDam(dam);
//        animal.setBreed(breed);
//        animal.setSire(sire);
//        animal.setCalvingDifficulty(calvingDifficulty);
//        animal.setAiORstockbull(aiORstockbull);
//        animal.setUser_id(user_id);
//
//        //inserts the data
//        addAnimalRef.set(animal).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    makeSnackBarMessage("Animal Added to Herd Successfully");
//                }
//                else{
//                    makeSnackBarMessage("Failed to add Animal to herd.");
//                }
//            }
//        });
//
//    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.animaloptionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AddAnimalActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddAnimalActivity.this, MainActivity.class));
            case R.id.menuAnimals:
                Toast.makeText(this, "Animals in Herd", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddAnimalActivity.this, AnimalActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public void getAnimal(){
        String id = db.collection("Animals").document().getId();
        db.collection("Animals").document(id).set(animal);

        Animal recievedAnimal = new Animal();

        recievedAnimal.setTagNumber(KEY_TAGNUMBER);
    }
}
