package com.example.farm;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farm.adapters.AnimalAdapter;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddAnimalActivity extends AppCompatActivity {
    private final String  TAG= "AddAnimalActivity";
    private EditText editTextTagNumber, editTextAnimalName, editTextDob, editTextSex, editTextDam, editTextCalvingDifficulty, editTextsire, editTextAiORstockbull, editTextBreed;
    private Button buttonSaveDetails;
    private Spinner spinnerGender, spinnerAiStockBull, spinnerCalvingDiff;
    View mParentLayout;
    private CircleImageView imgAnimalProfilePic;
    private Uri mainAnimalImageURI = null;
    private ProgressBar setupProgress;
    private List<Animal>animalList;
    private AnimalAdapter adapter;
    private Animal animal;

    private static final String KEY_TAGNUMBER = "tagNumber";
    private static final String KEY_ANIMALNAME = "animalName";
    private static final String KEY_DOB= "dob";
    private static final String KEY_GENDER= "gender";
    private static final String KEY_DAM = "dam";
    private static final String KEY_SIRE = "sire";
    private static final String KEY_AiOrStockbull = "aiORstockbull";
    private static final String KEY_CALVINGDIf = "calvingDifficulty";
    private static final String KEY_BREED = "breed";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_AnimalProfilePic = "animalProfilePic";


    //private StorageReference storageReference;
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
        imgAnimalProfilePic = findViewById(R.id.imgAnimalProfilePic);

        setupProgress = findViewById(R.id.addAnimal_progressBar);
        setupProgress.setVisibility(View.VISIBLE);

        addItemsOnSpinnerGender();
        addItemsOnSpinnerAiStockbull();
        addItemsOnSpinnerCalvingDiff();

        animalList = new ArrayList<>();

        mParentLayout = findViewById(android.R.id.content);

        //getting list of animals
        db.collection("animals").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //convert d to our animal object
                                Animal a = d.toObject(Animal.class);
                                animalList.add(a); //add all animals to list of animals
                            }
                            //adapter.notifyDataSetChanged();
                        }
                    }
                });
        }

    private void addItemsOnSpinnerCalvingDiff() {
        spinnerCalvingDiff= findViewById(R.id.spinnerCalvingDiff);
        List<String> listDifficulty = new ArrayList<String>();
        listDifficulty.add("0");
        listDifficulty.add("1");
        listDifficulty.add("2");
        listDifficulty.add("3");
        listDifficulty.add("4");
        listDifficulty.add("5");
        ArrayAdapter<String> calvingDiffAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listDifficulty);
        calvingDiffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCalvingDiff.setAdapter(calvingDiffAdapter);
    }

    public void addItemsOnSpinnerGender(){
        spinnerGender = findViewById(R.id.spinnerGender);
        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
    }

    private void addItemsOnSpinnerAiStockbull() {
        spinnerAiStockBull = findViewById(R.id.spinnerAiStockBull);
        List<String> listAiBull = new ArrayList<String>();
        listAiBull.add("AI");
        listAiBull.add("StockBull");
        ArrayAdapter<String> aiStockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listAiBull);
        aiStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAiStockBull.setAdapter(aiStockAdapter);

    }


    public void insertAnimal(View view){


        buttonSaveDetails = findViewById(R.id.buttonAddAnimal);


        buttonSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTag = editTextTagNumber.getText().toString().trim();
                String strName = editTextAnimalName.getText().toString().trim();
                String strDob = editTextDob.getText().toString().trim();
                String strSelectedGender = String.valueOf(spinnerGender.getSelectedItem());
                String strDam = editTextDam.getText().toString().trim();
                String strSelectedCalvingDif = String.valueOf(spinnerCalvingDiff.getSelectedItem());
                String strSire = editTextsire.getText().toString().trim();
                String strBreed = editTextBreed.getText().toString().trim();
                String strSelectedAIStockBull = String.valueOf(spinnerAiStockBull.getSelectedItem());
                String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
               // String strAnimalProfilePic =


                final Map<String, Object> animalMap = new HashMap<>();
                animalMap.put(KEY_TAGNUMBER, strTag);
                animalMap.put(KEY_ANIMALNAME, strName);
                animalMap.put(KEY_DOB, strDob);
                animalMap.put(KEY_GENDER, strSelectedGender);
                animalMap.put(KEY_DAM, strDam);
                animalMap.put(KEY_CALVINGDIf, strSelectedCalvingDif);
                animalMap.put(KEY_SIRE, strSire);
                animalMap.put(KEY_BREED, strBreed);
                animalMap.put(KEY_AiOrStockbull, strSelectedAIStockBull);
                animalMap.put(KEY_USERID, strUserID);
                //animalMap.put(KEY_AnimalProfilePic, );

                if(!hasValidationErrors(strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire) == true) {
                    db.collection("Animals")
                            .add(animalMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Animal inserted into herd with ID: " + documentReference.getId());

                                    Toast.makeText(AddAnimalActivity.this, "The animal profile has been created.", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(AddAnimalActivity.this, AnimalActivity.class);
                                    startActivity(mainIntent);
                                    finish();
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
            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(AddAnimalActivity.this);

    }




    private boolean hasValidationErrors(String tagNumber, String animalName, String dob, String selectedGender, String breed, String dam, String selectedCalvingDifficulty, String selectedAIStockBull, String sire){
        if (tagNumber.trim().isEmpty()) {
            editTextTagNumber.setError("Tag Number is required");
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
        }
        else if(selectedGender.isEmpty()){
            TextView errorText = (TextView)spinnerGender.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select the Aniamls Gender");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Animals Sex.");
            return true;
        }else if(dam.isEmpty()){
            editTextDam.setError("The Dam of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Dam.");
            return true;
        }else if(selectedCalvingDifficulty.isEmpty()){
            TextView errorText = (TextView)spinnerCalvingDiff.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select the Calving Difficulty number");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Calving Difficulty.");
            return true;
        }else if(sire.isEmpty()){
            editTextsire.setError("The Sire of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Sire.");
            return true;
        }else if(selectedAIStockBull.isEmpty()){
            TextView errorText = (TextView)spinnerAiStockBull.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select the Sire Type for the animal");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Sire Type.");
            return true;
        }else if(breed.isEmpty()){
            editTextBreed.setError("Breed of Animal is required");
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
