package com.example.farm.invoiceReceipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.AnimalActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class InsertInvoiceReceiptActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final String  TAG= "InsertAnimalActivity";
    private Button btnInsert;
    private ProgressBar progressBar;
    private Spinner spinnerType, spinnerCategory;
    private EditText editTextAmount, editTextDate;
    View mParentLayout;

    private ImageButton invoiceReceiptImage;
    private Uri mainImageURI = null;
    BottomNavigationView bottomNavigationView;

    private String user_id;
    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private Bitmap compressedImageFile;

    private static final String KEY_INVOICERECEIPTTYPE = "invoiceReceiptType";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_IMAGE= "image";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_AMOUNT="amount";
    private static final String KEY_DATE="date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_invoice_receipt);

        mParentLayout = findViewById(android.R.id.content);
        editTextAmount = findViewById(R.id.ediTextAmount);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDate.setFocusable(false);
        editTextDate.setKeyListener(null);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firestoreDB = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addItemsOnSpinnerInvoiceType();
        addItemsOnSpinnerCategory();

        invoiceReceiptImage = findViewById(R.id.invoiceOrReceiptImage);
        btnInsert = findViewById(R.id.btnInsert);
        progressBar = findViewById(R.id.progressBar);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dobCalendar = Calendar.getInstance();
                new DatePickerDialog(InsertInvoiceReceiptActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                        editTextDate.setText(date);
                    }
                }, dobCalendar
                        .get(Calendar.YEAR), dobCalendar.get(Calendar.MONTH),
                        dobCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        //Invoice will be inserted by clicking insert invoice button will call his)
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strSelectedType = String.valueOf(spinnerType.getSelectedItem());
                final String strSelectedCategory = String.valueOf(spinnerCategory.getSelectedItem());
                final String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
                final double amount = Double.parseDouble(editTextAmount.getText().toString());
                final String strDate = editTextDate.getText().toString().trim();

                if (mainImageURI != null) {
                    btnInsert.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();

                        File newImageFile = new File(mainImageURI.getPath());

                        try {
                            compressedImageFile = new Compressor(InsertInvoiceReceiptActivity.this)
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
                                            storeFirestore(isChanged, strUserID, strSelectedType, strSelectedCategory, downloadUrl, amount, strDate);

                                        }
                                    });
                                } else {
                                    btnInsert.setEnabled(true);
                                    Toast.makeText(InsertInvoiceReceiptActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        makeSnackBarMessage("Please select invoice/ receipt image");
                    }
                } else {
                    makeSnackBarMessage("Please select invoice/ receipt image");
                }

            }

        });


        invoiceReceiptImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(InsertInvoiceReceiptActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(InsertInvoiceReceiptActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(InsertInvoiceReceiptActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(InsertInvoiceReceiptActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(InsertInvoiceReceiptActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(InsertInvoiceReceiptActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(InsertInvoiceReceiptActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(InsertInvoiceReceiptActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(InsertInvoiceReceiptActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    private void storeFirestore(boolean isInvoice,String strUserID,String strInvoiceReceiptType,String strCategory,String downloadUrl, double amount, String date) {


        Map<String, Object> invoiceMap = new HashMap<>();
        invoiceMap.put(KEY_INVOICERECEIPTTYPE, strInvoiceReceiptType);
        invoiceMap.put(KEY_CATEGORY, strCategory);
        invoiceMap.put(KEY_USERID, strUserID);
        invoiceMap.put(KEY_IMAGE, downloadUrl);
        invoiceMap.put(KEY_AMOUNT, amount);
        invoiceMap.put(KEY_DATE, date);

        if (!hasValidationErrors(strInvoiceReceiptType, strCategory, isInvoice, date)) {

            firestoreDB.collection("InvoiceReceipts")
                    .add(invoiceMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnInsert.setEnabled(true);
                            Log.d(TAG, "Invoice inserted into herd with ID: " + documentReference.getId());
                            Toast.makeText(InsertInvoiceReceiptActivity.this, "Successfully added. ", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(InsertInvoiceReceiptActivity.this, InvoiceReceiptActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnInsert.setEnabled(true);
                            Log.w(TAG, "Error occured: ", e);
                            Toast.makeText(InsertInvoiceReceiptActivity.this, "Error : " + e, Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private boolean hasValidationErrors(String selectedType, String selectedCategory, boolean isInvoice, String date){
        if(selectedType.isEmpty()){
            TextView errorText = (TextView)spinnerType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select invoice/ receipt");
            makeSnackBarMessage("Please insert invoice/ receipt");
            return true;
        }
        if(selectedCategory.isEmpty()){
            TextView errorText = (TextView)spinnerCategory.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select a category");
            makeSnackBarMessage("Please insert the type of category");
            return true;
        }
        else if(!isInvoice)
        {
            makeSnackBarMessage("Please select image of invoice/ receipt ");
            return true;
        }else if (date.isEmpty()){
            editTextDate.setError("Please select a Date");
            editTextDate.setTextColor(Color.RED);
            makeSnackBarMessage("Please insert a Date");
            return true;
        }
        else{
            return false;
        }

    }

    public void addItemsOnSpinnerInvoiceType(){
        spinnerType = findViewById(R.id.spinnerType);
        List<String> list = new ArrayList<String>();
        list.add("Invoice");
        list.add("Receipt");

        ArrayAdapter<String> invoiceTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        invoiceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(invoiceTypeAdapter);
    }

    public void addItemsOnSpinnerCategory(){
        spinnerCategory = findViewById(R.id.spinnerCategory);
        List<String> list = new ArrayList<String>();
        list.add("Livestock");
        list.add("Hardware");
        list.add("Feed");
        list.add("Machinery");
        list.add("Contractors");
        list.add("Supplies");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.OFF)
                .start(InsertInvoiceReceiptActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                invoiceReceiptImage.setImageURI(mainImageURI);
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