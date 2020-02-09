package com.example.farm;

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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;



public class InsertAnimalActivity extends AppCompatActivity {
    private final String  TAG= "InsertAnimalActivity";
    private EditText  editTextTagNumber, editTextAnimalName, editTextDob, editTextSex, editTextDam, editTextCalvingDifficulty, editTextsire, editTextAiORstockbull, editTextBreed;
    private Button btnInsertAnimal;
    private ProgressBar addanimalProgress;
    private Spinner spinnerGender, spinnerAiStockBull, spinnerCalvingDiff;
    View mParentLayout;

    private CircleImageView animalProfilePic;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;

    private Bitmap compressedImageFile;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_animal);


        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Adding Animal to Herd");

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firestoreDB = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addItemsOnSpinnerGender();
        addItemsOnSpinnerAiStockbull();
        addItemsOnSpinnerCalvingDiff();

        editTextAnimalName = findViewById(R.id.editTextName);
        editTextDam = findViewById(R.id.editTextDAM);
        editTextDob = findViewById(R.id.editTextDob);
        editTextSex = findViewById(R.id.editTextSex);
        editTextCalvingDifficulty = findViewById(R.id.editTextCalvingDifficulty);
        editTextsire =findViewById(R.id.ediTextSire);
        editTextBreed =findViewById(R.id.editTextBreed);
        editTextAiORstockbull =findViewById(R.id.editTextAIBull);

        animalProfilePic = findViewById(R.id.animal_image);
        editTextTagNumber = findViewById(R.id.tagNumber);
        btnInsertAnimal = findViewById(R.id.btnInsertAnimal);
        addanimalProgress = findViewById(R.id.animaml_progress);

        addanimalProgress.setVisibility(View.VISIBLE);
        btnInsertAnimal.setEnabled(false);


        firestoreDB.collection("animals").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String tagNumber = task.getResult().getString("tagNumber");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        editTextTagNumber.setText(tagNumber);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.animalsmall);

                        Glide.with(InsertAnimalActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(animalProfilePic);


                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(InsertAnimalActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

                addanimalProgress.setVisibility(View.INVISIBLE);
                btnInsertAnimal.setEnabled(true);

            }
        });


        btnInsertAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strTag = editTextTagNumber.getText().toString();
                final String strName = editTextAnimalName.getText().toString().trim();
                final String strDob = editTextDob.getText().toString().trim();
                final String strSelectedGender = String.valueOf(spinnerGender.getSelectedItem());
                final String strDam = editTextDam.getText().toString().trim();
                final String strSelectedCalvingDif = String.valueOf(spinnerCalvingDiff.getSelectedItem());
                final String strSire = editTextsire.getText().toString().trim();
                final String strBreed = editTextBreed.getText().toString().trim();
                final String strSelectedAIStockBull = String.valueOf(spinnerAiStockBull.getSelectedItem());
                final String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();

                if (!TextUtils.isEmpty(strTag) && mainImageURI != null) {

                    addanimalProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        File newImageFile = new File(mainImageURI.getPath());

                        try {

                            compressedImageFile = new Compressor(InsertAnimalActivity.this)
                                    .setMaxHeight(125)
                                    .setMaxWidth(125)
                                    .setQuality(50)
                                    .compressToBitmap(newImageFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();

                   final StorageReference filePath = storageReference.child("images/"+ UUID.randomUUID().toString());


                        filePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){

                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task){
                                if(task.isSuccessful())

                                {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                       public void onSuccess(Uri uri)
                                        {
                                           String downloadUrl = uri.toString();
                                            storeFirestore(downloadUrl, strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire, strUserID);

                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(InsertAnimalActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    }
                else
                    {

                        storeFirestore(null, strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire, strUserID);

                    }

                }

            });



        animalProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(InsertAnimalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(InsertAnimalActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(InsertAnimalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }

            }

        });


}

    private void storeFirestore(String downloadUrl, String strTag, String strName, String strDob, String strSelectedGender, String strBreed, String strDam, String strSelectedCalvingDif, String strSelectedAIStockBull, String strSire, String strUserID) {


        Map<String, Object> animalMap = new HashMap<>();
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
        animalMap.put(KEY_AnimalProfilePic, downloadUrl);



        if(!hasValidationErrors(strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire)) {

            firestoreDB.collection("animals")
                    .add(animalMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Animal inserted into herd with ID: " + documentReference.getId());

                            Toast.makeText(InsertAnimalActivity.this, "The animal profile has been created.", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(InsertAnimalActivity.this, AnimalActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding animal into herd", e);
                            Toast.makeText(InsertAnimalActivity.this, "(FIRESTORE Error) : " + e, Toast.LENGTH_LONG).show();
                        }
                    });


        }
                addanimalProgress.setVisibility(View.INVISIBLE);

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


    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(InsertAnimalActivity.this);

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

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }


    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}