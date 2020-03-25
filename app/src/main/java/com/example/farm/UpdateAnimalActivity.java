package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAnimalActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "UpdateAnimalActivity";
    private EditText editTextTagNumberUpdate, editTextAnimalNameUpdate, editTextDobUpdate, editTextDamUpdate, editTextCalvingDifficultyUpdate, editTextSireUpdate, editTextAiORstockbullUpdate, editTextBreedUpdate;
    private Button buttonUpdate;
    private Spinner spinnerGenderUpdate, spinnerCalvingDiffUpdate, spinnerAiStockBullUpdate;
    private String docId;
    private ImageButton animalProfilePic;
    private Animal animal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage fireBaseStorage;
    StorageReference storageReference;
BottomNavigationView bottomNavigationView;
    View mParentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_animal);

        addItemsOnSpinnerGender();
        addItemsOnSpinnerAiStockbull();
        addItemsOnSpinnerCalvingDiff();

        fireBaseStorage= FirebaseStorage.getInstance();


        animal = (Animal) getIntent().getSerializableExtra("animal");
        docId = (String) getIntent().getSerializableExtra("documentId");
        // initialize
        editTextTagNumberUpdate = findViewById(R.id.editTextTagNumber);
        editTextAnimalNameUpdate = findViewById(R.id.editTextName);
        editTextDamUpdate = findViewById(R.id.editTextDAM);
        editTextDobUpdate = findViewById(R.id.editTextDob);
        spinnerGenderUpdate = findViewById(R.id.spinnerGender);
        spinnerCalvingDiffUpdate = findViewById(R.id.spinnerCalvingDiff);
        editTextSireUpdate = findViewById(R.id.ediTextSire);
        editTextBreedUpdate = findViewById(R.id.editTextBreed);
        spinnerAiStockBullUpdate = findViewById(R.id.spinnerAiStockBull);
        animalProfilePic= findViewById(R.id.imageAnimalProfilePic);
        buttonUpdate = findViewById(R.id.buttonUpdateAnimal);
        //populate animal data before update
        storageReference = fireBaseStorage.getReferenceFromUrl(animal.getAnimalProfilePic());

        Glide.with(this)
                .load(storageReference)
                .into(animalProfilePic);

        editTextTagNumberUpdate.setText(animal.getTagNumber());
        editTextAnimalNameUpdate.setText(animal.getAnimalName());
        editTextDobUpdate.setText(animal.getDob());
        spinnerGenderUpdate.setSelection(getIndex(spinnerGenderUpdate,animal.getGender()));
        editTextDamUpdate.setText(animal.getDam());
        spinnerCalvingDiffUpdate.setSelection(getIndex(spinnerCalvingDiffUpdate,animal.getCalvingDifficulty()));
        editTextSireUpdate.setText(animal.getSire());
        editTextBreedUpdate.setText(animal.getBreed());
        spinnerAiStockBullUpdate.setSelection(getIndex(spinnerAiStockBullUpdate,animal.getAiORstockbull()));

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_animals);
        bottomNavigationView.setOnNavigationItemSelectedListener(UpdateAnimalActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(UpdateAnimalActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(UpdateAnimalActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(UpdateAnimalActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(UpdateAnimalActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(UpdateAnimalActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    private boolean hasValidationErrors(String tagNumber, String animalName, String dob, String gender, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire) {
        if (tagNumber.trim().isEmpty()) {
            editTextTagNumberUpdate.setError("TagNumber is required");
            makeSnackBarMessage("Please insert Tag Number.");
            return true;
        } else if (animalName.isEmpty()) {
            editTextAnimalNameUpdate.setError("Animal Name Required");
            makeSnackBarMessage("Please insert Animal Name.");
            return true;
        } else if (dob.isEmpty()) {
            editTextDobUpdate.setError("Date of Birth is required.");
            makeSnackBarMessage("Please insert Date of Birth.");
            return true;
        }  else if(gender.isEmpty()){
            TextView errorText = (TextView)spinnerGenderUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select the Aniamls Gender");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Animal's Gender.");
            return true;
        } else if (dam.isEmpty()) {
            editTextDamUpdate.setError("The Dam of the Animal is required");
            makeSnackBarMessage("Please insert the Animal's Dam.");
            return true;
        } else if(calvingDifficulty.isEmpty()){
            TextView errorText = (TextView)spinnerCalvingDiffUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select the Calving Difficulty number");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Calving Difficulty.");
            return true;
        } else if (sire.isEmpty()) {
            editTextSireUpdate.setError("The Sire of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Sire.");
            return true;
        } else if(aiORstockbull.isEmpty()){
            TextView errorText = (TextView)spinnerAiStockBullUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select the Sire Type for the animal");//changes the selected item text to this
            makeSnackBarMessage("Please insert the Sire Type.");
            return true;
        } else if (breed.isEmpty()) {
            editTextBreedUpdate.setError("Breed of Animal is Required");
            makeSnackBarMessage("Please insert Animal Breed.");
            return true;
        } else {
            return false;
        }

    }

//    public void updateAnimal(View view)    {
//       // Animal animal = createAnimalObject();
//        updateAnimalToCollection(animal);
//    }
//
//    private Animal createAnimalObject(){
//        final Animal animal = new Animal();
//        return animal;
//    }
//
//    private void animalIdToRefDocId(String tagNumber){
//           db.collection("Animals").whereEqualTo("tagNumber", tagNumber).get()
//           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if(task.isSuccessful()){
//                        List<Animal> animalList = new ArrayList<>();
//
//                        for(DocumentSnapshot doc: task.getResult()){
//                         Animal a = doc.toObject(Animal.class);
//                         a.setId(doc.getId());
//                         animalList.add(a);
//                        }
//                     }
//                }
//            });
//
//    }

    public void updateAnimal(View view) {

        String strTag = editTextTagNumberUpdate.getText().toString().trim();
        String strName = editTextAnimalNameUpdate.getText().toString().trim();
        String strDob = editTextDobUpdate.getText().toString().trim();
        String strGender = String.valueOf(spinnerGenderUpdate.getSelectedItem());
        String strDam = editTextDamUpdate.getText().toString().trim();
        String strCalvingDif = editTextCalvingDifficultyUpdate.getText().toString().trim();
        String strSire = editTextSireUpdate.getText().toString().trim();
        String strBreed = editTextBreedUpdate.getText().toString().trim();
        String strAiOrStockbull = editTextAiORstockbullUpdate.getText().toString().trim();

        if (!hasValidationErrors(strTag, strName, strDob, strGender, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire)) {

            db.collection("animals").document(docId)
                    .update(
                            "tagNumber", strTag,
                            "animalName", strName,
                            "dob", strDob,
                            "gender", strGender,
                            "dam", strDam,
                            "sire", strSire,
                            "aiORstockbull", strAiOrStockbull,
                            "calvingDifficulty", strCalvingDif,
                            "breed", strBreed
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateAnimalActivity.this, "Animal Updated", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateAnimalActivity.this, AnimalActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Failure to update animal due to: " + e.toString());
                }
            });
        }
    }


    //this gets the whole collection
    public void geAlltAnimals() {
        db.collection("animals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void addItemsOnSpinnerCalvingDiff() {
    spinnerCalvingDiffUpdate= findViewById(R.id.spinnerCalvingDiff);
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
    spinnerCalvingDiffUpdate.setAdapter(calvingDiffAdapter);
    }

    private void addItemsOnSpinnerGender() {
        spinnerGenderUpdate = findViewById(R.id.spinnerGender);
        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenderUpdate.setAdapter(genderAdapter);
    }

    private void addItemsOnSpinnerAiStockbull() {
        spinnerAiStockBullUpdate = findViewById(R.id.spinnerAiStockBull);
        List<String> listAiBull = new ArrayList<String>();
        listAiBull.add("AI");
        listAiBull.add("StockBull");

        ArrayAdapter<String> aiStockAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listAiBull);
        aiStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAiStockBullUpdate.setAdapter(aiStockAdapter);
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return -1;
    }
}





//                 .whereEqualTo("tagNumber", strTag).get()
//                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//@Override
//public void onComplete(@NonNull Task<QuerySnapshot> task) {
//        if(task.isSuccessful()){
//        List<Animal> animalList = new ArrayList<>();
//
//        for(DocumentSnapshot doc: task.getResult()){
//        Animal a = doc.toObject(Animal.class);
//        a.setId(doc.getId());
//        animalList.add(a);
//        docId = a.getId();
//        }
//        }
//        }
//        });
//
//        db.collection("Animals")
//        .document()
//        .set(animal, SetOptions.merge())