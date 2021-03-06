package com.example.farm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farm.dueDateAlarm.OnReceive;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static com.example.farm.dueDateAlarm.UtilHelper.getDeliveryDate;
import static com.example.farm.dueDateAlarm.UtilHelper.getNotifyDate;
import static com.example.farm.dueDateAlarm.UtilHelper.getNotifyDateStr;


public class InsertAnimalActivity extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {
    private final String  TAG= "InsertAnimalActivity";
    private EditText  editTextTagNumber, editTextAnimalName, editTextDob, editTextDam, editTextsire, editTextBreed, editTextDateOfInsemination;
    private Button btnInsertAnimal;
    private TextView textViewDateCalculatedCalveAndDelivery;
    private ProgressBar addanimalProgress;
    private Spinner spinnerGender, spinnerAiStockBull, spinnerCalvingDiff;
    private CheckBox checkBoxInCalve;
    ConstraintLayout constraintLayout;
    BottomNavigationView bottomNavigationView;
    private ImageButton animalProfilePic;
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
    private static final String KEY_IN_CALVE = "inCalve";
    private static final String KEY_DOI = "doi";
    private static final String KEY_DOC = "doc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_animal);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        constraintLayout= findViewById(R.id.constraintLayout);
        firestoreDB = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addItemsOnSpinnerGender();
        addItemsOnSpinnerAiStockbull();
        addItemsOnSpinnerCalvingDiff();

        editTextAnimalName = findViewById(R.id.editTextName);
        editTextDam = findViewById(R.id.editTextDAM);
        editTextDob = findViewById(R.id.editTextDob);
        editTextDob.setFocusable(false);
        editTextDob.setKeyListener(null);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerCalvingDiff = findViewById(R.id.spinnerCalvingDiff);
        editTextsire =findViewById(R.id.editTextSire);
        editTextBreed =findViewById(R.id.editTextBreed);
        spinnerAiStockBull =findViewById(R.id.spinnerAiStockBull);
        animalProfilePic = findViewById(R.id.animal_image);
        editTextTagNumber = findViewById(R.id.editTexttagNumber);
        btnInsertAnimal = findViewById(R.id.btnInsertAnimal);
        addanimalProgress = findViewById(R.id.progressBar);
        checkBoxInCalve = findViewById(R.id.checkBoxInCalve);
        editTextDateOfInsemination = findViewById(R.id.textViewDateOfInsemination);
        editTextDateOfInsemination.setFocusable(false);
        editTextDateOfInsemination.setKeyListener(null);
        textViewDateCalculatedCalveAndDelivery = findViewById(R.id.textViewDateCalculatedCalveAndDelivery);

        //Check point for in calve check or uncheck
        checkBoxInCalve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    editTextDateOfInsemination.setVisibility(View.VISIBLE);
                    textViewDateCalculatedCalveAndDelivery.setVisibility(View.GONE);
                }
                else {
                    editTextDateOfInsemination.setVisibility(View.GONE);
                    textViewDateCalculatedCalveAndDelivery.setVisibility(View.GONE);
                }
            }
        });
        editTextDateOfInsemination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                new DatePickerDialog(InsertAnimalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        editTextDateOfInsemination.setText(date);
                        //Calculating Expected, delivery and notification date
                        textViewDateCalculatedCalveAndDelivery.setText("Expected Delivery date is on "+getDeliveryDate(date)+" and you will be notified on "+getNotifyDateStr(date));
                        textViewDateCalculatedCalveAndDelivery.setVisibility(View.VISIBLE);
                    }
                }, now
                        .get(Calendar.YEAR), now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dobCalendar = Calendar.getInstance();
                new DatePickerDialog(InsertAnimalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        editTextDob.setText(date);
                    }
                }, dobCalendar
                        .get(Calendar.YEAR), dobCalendar.get(Calendar.MONTH),
                        dobCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    checkBoxInCalve.setVisibility(View.VISIBLE);
                    editTextDateOfInsemination.setVisibility(View.GONE);
                    textViewDateCalculatedCalveAndDelivery.setVisibility(View.GONE);
                }
                else{
                    checkBoxInCalve.setChecked(false);
                    checkBoxInCalve.setVisibility(View.GONE);
                    editTextDateOfInsemination.setText("");
                    editTextDateOfInsemination.setVisibility(View.GONE);
                    textViewDateCalculatedCalveAndDelivery.setText("");
                    textViewDateCalculatedCalveAndDelivery.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                    checkBoxInCalve.setVisibility(View.GONE);
            }
        });

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

                        Glide.with(InsertAnimalActivity.this)
                                .setDefaultRequestOptions(placeholderRequest)
                                .load(image).into(animalProfilePic);
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
                Animal animal = new Animal();
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
                final String dateOfInsemination = editTextDateOfInsemination.getText().toString().trim();
                final boolean inCalve= checkBoxInCalve.isChecked();
                animal.setTimeAdded(new Timestamp(new Date()));
                final String strRegisteredTimestamp = String.valueOf(animal.getTimeAdded());

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

                   final StorageReference filePath = storageReference.child("images/"+ UUID.randomUUID().toString());

                        filePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){

                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task){
                                if(task.isSuccessful()) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                       public void onSuccess(Uri uri)
                                        {
                                           String downloadUrl = uri.toString();
                                            storeFirestore(downloadUrl, strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire, strUserID,inCalve,dateOfInsemination);
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(InsertAnimalActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    } else {
                        storeFirestore(null, strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire, strUserID,inCalve, dateOfInsemination);
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
            bottomNavigationView = findViewById(R.id.bottom_navigation_view);
            bottomNavigationView.setSelectedItemId(R.id.ic_animals);
            bottomNavigationView.setOnNavigationItemSelectedListener(InsertAnimalActivity.this);
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(InsertAnimalActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(InsertAnimalActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(InsertAnimalActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(InsertAnimalActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(InsertAnimalActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    private void storeFirestore(String downloadUrl, final String strTag, final String strName, String strDob, String strSelectedGender, String strBreed, String strDam, String strSelectedCalvingDif, String strSelectedAIStockBull, String strSire, String strUserID,boolean inCalve,String dateOfInsemination) {

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
            animalMap.put(KEY_AnimalProfilePic, downloadUrl);
             if(inCalve && spinnerGender.getSelectedItemPosition()==1) {
                animalMap.put(KEY_IN_CALVE, true);
                animalMap.put(KEY_DOI,dateOfInsemination);
                animalMap.put(KEY_DOC,textViewDateCalculatedCalveAndDelivery.getText().toString());
            } else {
                animalMap.put(KEY_IN_CALVE, false);
                animalMap.put(KEY_DOI,"");
                animalMap.put(KEY_DOC,"");
             }

            if ((!hasValidationErrors(strTag, strName, strDob, strSelectedGender, strBreed, strDam, strSelectedCalvingDif, strSelectedAIStockBull, strSire,inCalve,dateOfInsemination))){

                insertAnimal(animalMap,strName,strTag);
            }
            addanimalProgress.setVisibility(View.INVISIBLE);
        }




        private boolean hasValidationErrors(String tagNumber, String animalName, String dob, String selectedGender, String breed, String dam, String selectedCalvingDifficulty, String selectedAIStockBull, String sire,boolean inCalve,String dateOfInsemination) {
            if (tagNumber.trim().isEmpty()) {
                editTextTagNumber.setError("Tag Number is required");
                makeSnackBarMessage("Please insert Tag Number.");
                return true;
            } else if (animalName.isEmpty()) {
                editTextAnimalName.setError("Animal Name Required");
                makeSnackBarMessage("Please insert Animal Name.");
                return true;
            } else if (dob.isEmpty()) {
                editTextDob.setError("Date of Birth is required.");
                makeSnackBarMessage("Please insert Date of Birth.");
                return true;
            } else if (selectedGender.isEmpty()) {
                TextView errorText = (TextView) spinnerGender.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Please select the Aniamls Gender");
                makeSnackBarMessage("Please insert the Animals Sex.");
                return true;
            } else if (dam.isEmpty()) {
                editTextDam.setError("The Dam of the Animal is required");
                makeSnackBarMessage("Please insert the Animals Dam.");
                return true;
            } else if (selectedCalvingDifficulty.isEmpty()) {
                TextView errorText = (TextView) spinnerCalvingDiff.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Select the Calving Difficulty number");
                makeSnackBarMessage("Please insert the Calving Difficulty.");
                return true;
            } else if (sire.isEmpty()) {
                editTextsire.setError("The Sire of the Animal is required");
                makeSnackBarMessage("Please insert the Animals Sire.");
                return true;
            } else if (selectedAIStockBull.isEmpty()) {
                TextView errorText = (TextView) spinnerAiStockBull.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Please select the Sire Type for the animal");
                makeSnackBarMessage("Please insert the Sire Type.");
                return true;
            } else if (breed.isEmpty()) {
                editTextBreed.setError("Breed of Animal is required");
                makeSnackBarMessage("Please insert Animal Breed.");
                return true;
            } else if (inCalve && dateOfInsemination.isEmpty()){
                editTextDateOfInsemination.setError("Date of Insemination is required");
                makeSnackBarMessage("If inCalve is checked insemination date must be entered");
                return true;
            }
            else {
                return false;
            }
        }

    private void insertAnimal(final Map<String, Object> animalMap,final String strName,final String strTag){

        firestoreDB.collection("animals")
                .whereEqualTo("tagNumber", strTag)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot= task.getResult();
                           if (snapshot.size()==0){
                               firestoreDB.collection("animals")
                                       .add(animalMap)
                                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                           @Override
                                           public void onSuccess(DocumentReference documentReference) {
                                               //Alarm will set here after successful animal insertion
                                               setAlarm(strName, strTag);
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
                                               Toast.makeText(InsertAnimalActivity.this, "(FIRESTORE Error) : " + e, Toast.LENGTH_LONG).show(); }
                                       });
                           }
                           else {
                               Toast.makeText(InsertAnimalActivity.this, "Tag Number already exists", Toast.LENGTH_LONG).show();
                           }
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
                result.getError();
            }
        }
    }

    private void makeSnackBarMessage( String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void setAlarm(String strName, String strTag)
    {
        if(checkBoxInCalve.isChecked() && !editTextDateOfInsemination.getText().toString().equals(getString(R.string.date_of_insemination))) {
            Intent intent = new Intent(InsertAnimalActivity.this, OnReceive.class);
            intent.putExtra("title", "Calving Date notification");
            intent.putExtra("message", strName + " " + strTag + " is due to calve in/on  " + getDeliveryDate(editTextDateOfInsemination.getText().toString()));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(InsertAnimalActivity.this, 2020, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + (getNotifyDate(editTextDateOfInsemination.getText().toString()).getTime()-new Date().getTime()), pendingIntent);
            Log.d("Alarm Date", "calving alarm "+ getNotifyDate(editTextDateOfInsemination.getText().toString()));
        }
    }


}