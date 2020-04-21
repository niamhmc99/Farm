package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UpdateAnimalActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "UpdateAnimalActivity";
    private EditText editTextTagNumberUpdate, editTextAnimalNameUpdate, editTextDobUpdate, editTextDamUpdate, editTextCalvingDifficultyUpdate, editTextSireUpdate, editTextAiORstockbullUpdate, editTextBreedUpdate;
    private Button buttonUpdate;
    private Spinner spinnerGenderUpdate, spinnerCalvingDiffUpdate, spinnerAiStockBullUpdate;
    private String docId, animalImageUri;
    private TextView textViewDateOfInsemination, textViewDateCalculatedCalveAndDelivery;
    private ImageButton animalProfilePic;
    private Animal animal;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore firestoreDB;
    BottomNavigationView bottomNavigationView;
    View mParentLayout;
    private String user_id;
    private Uri mainImageURI = null;
    private boolean isChanged = false;
    private Bitmap compressedImageFile;
    private CheckBox checkBoxInCalve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_animal);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addItemsOnSpinnerGender();
        addItemsOnSpinnerAiStockbull();
        addItemsOnSpinnerCalvingDiff();

        firestoreDB = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        animal = (Animal) getIntent().getSerializableExtra("animal");
        docId = (String) getIntent().getSerializableExtra("documentId");
        editTextTagNumberUpdate = findViewById(R.id.editTextTagNumber);
        editTextAnimalNameUpdate = findViewById(R.id.editTextName);
        editTextDamUpdate = findViewById(R.id.editTextDAM);
        editTextDobUpdate = findViewById(R.id.editTextDob);
        spinnerGenderUpdate = findViewById(R.id.spinnerGender);
        spinnerCalvingDiffUpdate = findViewById(R.id.spinnerCalvingDiff);
        editTextSireUpdate = findViewById(R.id.editTextSire);
        editTextBreedUpdate = findViewById(R.id.editTextBreed);
        spinnerAiStockBullUpdate = findViewById(R.id.spinnerAiStockBull);
        buttonUpdate = findViewById(R.id.buttonUpdateAnimal);
        checkBoxInCalve = findViewById(R.id.checkBoxInCalve);
        textViewDateOfInsemination = findViewById(R.id.textViewDateOfInsemination);
        textViewDateCalculatedCalveAndDelivery = findViewById(R.id.textViewDateCalculatedCalveAndDelivery);
        editTextTagNumberUpdate.setText(animal.getTagNumber());
        editTextAnimalNameUpdate.setText(animal.getAnimalName());
        editTextDobUpdate.setText(animal.getDob());
        spinnerGenderUpdate.setSelection(getIndex(spinnerGenderUpdate, animal.getGender()));
        editTextDamUpdate.setText(animal.getDam());
        spinnerCalvingDiffUpdate.setSelection(getIndex(spinnerCalvingDiffUpdate, animal.getCalvingDifficulty()));
        editTextSireUpdate.setText(animal.getSire());
        editTextBreedUpdate.setText(animal.getBreed());
        spinnerAiStockBullUpdate.setSelection(getIndex(spinnerAiStockBullUpdate, animal.getAiORstockbull()));
        checkBoxInCalve.setText(animal.getInCalve());
        animalImageUri=  animal.getAnimalProfilePic();
        setAnimalImage(animalImageUri);

        animalProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(UpdateAnimalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(UpdateAnimalActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(UpdateAnimalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }

        });

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_animals);
        bottomNavigationView.setOnNavigationItemSelectedListener(UpdateAnimalActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
        } else if (gender.isEmpty()) {
            TextView errorText = (TextView) spinnerGenderUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select the Aniamls Gender");
            makeSnackBarMessage("Please insert the Animal's Gender.");
            return true;
        } else if (dam.isEmpty()) {
            editTextDamUpdate.setError("The Dam of the Animal is required");
            makeSnackBarMessage("Please insert the Animal's Dam.");
            return true;
        } else if (calvingDifficulty.isEmpty()) {
            TextView errorText = (TextView) spinnerCalvingDiffUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Select the Calving Difficulty number");
            makeSnackBarMessage("Please insert the Calving Difficulty.");
            return true;
        } else if (sire.isEmpty()) {
            editTextSireUpdate.setError("The Sire of the Animal is required");
            makeSnackBarMessage("Please insert the Animals Sire.");
            return true;
        } else if (aiORstockbull.isEmpty()) {
            TextView errorText = (TextView) spinnerAiStockBullUpdate.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select the Sire Type for the animal");
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


    public void updateAnimal(View view) {

        final String strTag = editTextTagNumberUpdate.getText().toString().trim();
        final String strName = editTextAnimalNameUpdate.getText().toString().trim();
        final String strDob = editTextDobUpdate.getText().toString().trim();
        final String strGender = String.valueOf(spinnerGenderUpdate.getSelectedItem());
        final String strDam = editTextDamUpdate.getText().toString().trim();
        final String strCalvingDif = String.valueOf(spinnerCalvingDiffUpdate.getSelectedItemId());
        final String strSire = editTextSireUpdate.getText().toString().trim();
        final String strBreed = editTextBreedUpdate.getText().toString().trim();
        final String strAiOrStockbull = String.valueOf(spinnerAiStockBullUpdate.getSelectedItemId());
        final String strInCalve = String.valueOf(checkBoxInCalve.isSelected());

        if (!TextUtils.isEmpty(strTag) && mainImageURI != null) {
            if (isChanged) {
                user_id = firebaseAuth.getCurrentUser().getUid();
                File newImageFile = new File(mainImageURI.getPath());
                try {
                    compressedImageFile = new Compressor(UpdateAnimalActivity.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                final StorageReference filePath = storageReference.child("images/" + UUID.randomUUID().toString());

                filePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    updateFirestore(downloadUrl,strTag, strName, strDob, strGender, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire,strInCalve);
                                }
                            });
                        } else {
                            Toast.makeText(UpdateAnimalActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } else {
            updateFirestore("",strTag, strName, strDob, strGender, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire,strInCalve);
        }
    }

    private void updateFirestore(String downloadUrl,String strTag, String strName, String strDob, String strGender, String strBreed, String strDam, String strCalvingDif, String strAiOrStockbull, String strSire,String strInCalve){


    if(!hasValidationErrors(strTag, strName, strDob, strGender, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire))

    {

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
                        "breed", strBreed,
                        "inCalve", strInCalve,
                        "animalProfilePic", downloadUrl
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

    private void setAnimalImage(String animalImageUrl)
    {
        animalProfilePic= findViewById(R.id.animal_image);

        RequestOptions placeholderOption= new RequestOptions();
        placeholderOption.placeholder(R.drawable.animalsmall);

        //Preconditions.checkNotNull(mContext); -- this is throwing null pointer exception
        Glide.with(this)
                .setDefaultRequestOptions(placeholderOption).
                load(animalImageUrl).into(animalProfilePic);

    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(UpdateAnimalActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                animalProfilePic.setImageURI(mainImageURI);
                isChanged = true;
                setAnimalImage(mainImageURI.toString());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}