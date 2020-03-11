package com.example.farm.invoiceReceipt;

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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class InsertInvoiceReceiptActivity extends AppCompatActivity {

    private final String  TAG= "InsertAnimalActivity";
    private Button btnInsert;
    private ProgressBar progressBar;
    private Spinner spinnerType, spinnerCategory;
    View mParentLayout;

    private CircleImageView invoiceReceiptImage;
    private Uri mainImageURI = null;

    private String user_id;
    private boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private Bitmap compressedImageFile;

    private static final String KEY_INVOICERECEIPTTYPE = "invoiceType";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_IMAGE= "image";
    private static final String KEY_USERID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_invoice_receipt);

        mParentLayout = findViewById(android.R.id.content);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        firestoreDB = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        addItemsOnSpinnerInvoiceType();
        addItemsOnSpinnerCategory();

        invoiceReceiptImage = findViewById(R.id.invoiceOrReceiptImage);
        btnInsert = findViewById(R.id.btnInsert);
        progressBar = findViewById(R.id.progressBar);


        //Help-Comment (Invoice will be inserted by clicking insert invoice button will call his)
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strSelectedType = String.valueOf(spinnerType.getSelectedItem());
                final String strSelectedCategory = String.valueOf(spinnerCategory.getSelectedItem());
                final String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();

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
//                        byte[] thumbData = baos.toByteArray();

                        final StorageReference filePath = storageReference.child("images/" + UUID.randomUUID().toString());

                        filePath.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            storeFirestore(isChanged, strUserID, strSelectedType, strSelectedCategory, downloadUrl);

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
    }

    private void storeFirestore(boolean isInvoice,String strUserID,String strInvoiceReceiptType,String strCategory,String downloadUrl) {


        Map<String, Object> invoiceMap = new HashMap<>();
        invoiceMap.put(KEY_INVOICERECEIPTTYPE, strInvoiceReceiptType);
        invoiceMap.put(KEY_CATEGORY, strCategory);
        invoiceMap.put(KEY_USERID, strUserID);
        invoiceMap.put(KEY_IMAGE, downloadUrl);

        if (!hasValidationErrors(strInvoiceReceiptType, strCategory, isInvoice)) {

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

    private boolean hasValidationErrors(String selectedType, String selectedCategory, boolean isInvoice){
        if(selectedType.isEmpty()){
            TextView errorText = (TextView)spinnerType.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select the invoice type");//changes the selected item text to this
            makeSnackBarMessage("Please insert invoice type");
            return true;
        }
        if(selectedCategory.isEmpty()){
            TextView errorText = (TextView)spinnerCategory.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Please select a category");//changes the selected item text to this
            makeSnackBarMessage("Please insert the type of category");
            return true;
        }
        else if(!isInvoice)
        {
            makeSnackBarMessage("Please select image file ");
            return true;
        }
        else{
            return false;
        }

    }

    public void addItemsOnSpinnerInvoiceType(){
        spinnerType = findViewById(R.id.spinnerType);
        List<String> list = new ArrayList<String>();
        list.add("Income");
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