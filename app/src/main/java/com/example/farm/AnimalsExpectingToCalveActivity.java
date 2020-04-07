package com.example.farm;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.adapters.AnimalAdapter;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.fragments.AnimalDialogFragment;
import com.example.farm.fragments.CalvingDescriptionFragment;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Animal;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.client.android.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AnimalsExpectingToCalveActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "AnimalActivity";
    private List<Animal> animalList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference animalRef = db.collection("animals");
    private AnimalAdapter adapter;
    private FloatingActionButton mFabAddAnimal;
    View mParentLayout;
    ImageButton updateButton;
    TextView tvScanBarcode;
    BottomNavigationView bottomNavigationView;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private FirebaseAuth firebaseAuth;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);

        updateButton = findViewById(R.id.update_button);
        mFabAddAnimal = findViewById(R.id.fabInsertAnimal);
        mFabAddAnimal.setOnClickListener(this);
        mParentLayout = findViewById(android.R.id.content);
        tvScanBarcode = findViewById(R.id.tvScanBarcode);
        tvScanBarcode.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        setupFirebaseAuth();

        findViewById(R.id.fabInsertAnimal).setVisibility(View.GONE);
        tvScanBarcode.setVisibility(View.GONE);
        animalList = new ArrayList<>();
        db.collection("animals").whereEqualTo("inCalve","1").whereEqualTo("user_id",user_id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //convert d to our animal object
                                Animal a = d.toObject(Animal.class);
                                animalList.add(a);
                            }
                        }
                    }
                });
        setUpRecyclerView();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_animals);
        bottomNavigationView.setOnNavigationItemSelectedListener(AnimalsExpectingToCalveActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(AnimalsExpectingToCalveActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(AnimalsExpectingToCalveActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(AnimalsExpectingToCalveActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(AnimalsExpectingToCalveActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(AnimalsExpectingToCalveActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }


    private void setUpRecyclerView(){
        Query query = animalRef.orderBy("tagNumber", Query.Direction.DESCENDING).whereEqualTo("inCalve","1").whereEqualTo("user_id",user_id);

        FirestoreRecyclerOptions<Animal> options= new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();
        adapter = new AnimalAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                //adapter.deleteItem(viewHolder.getAdapterPosition());
                // Toast.makeText(AnimalActivity.this, "Animal Has Been Deleted from Herd.", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(AnimalsExpectingToCalveActivity.this);
                builder.setTitle("Are you sure about this?");
                builder.setMessage("Deletion is permanent...");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteItem(viewHolder.getAdapterPosition());
                    }
                });//
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();


            }
        }).attachToRecyclerView(recyclerView);

        db.collection("animals").whereEqualTo("inCalve","1").whereEqualTo("user_id",user_id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Animal a = d.toObject(Animal.class);
                                animalList.add(a);
                            }
                        }
                    }
                });

        adapter.setOnItemClickListener(new AnimalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, final int position) {
                final Animal animal = documentSnapshot.toObject(Animal.class);

                final String docId = documentSnapshot.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(AnimalsExpectingToCalveActivity.this);

                builder.setTitle("Choose option");
                builder.setMessage("Add Calving Information' Information?");
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CalvingDescriptionFragment calvingDescriptionFragment=new CalvingDescriptionFragment();
                        calvingDescriptionFragment.setAnimal(animal);
                        calvingDescriptionFragment.setDocId(docId);
                        calvingDescriptionFragment.show(getSupportFragmentManager(), CalvingDescriptionFragment.class.getSimpleName());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumainopts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AnimalsExpectingToCalveActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnimalsExpectingToCalveActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvScanBarcode:
                checkPermission(Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_CODE);
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
                break;
            case R.id.fabInsertAnimal:
                startActivity(new Intent(AnimalsExpectingToCalveActivity.this, InsertAnimalActivity.class));
                break;

            case R.id.update_button:
                startActivity(new Intent(AnimalsExpectingToCalveActivity.this, UpdateAnimalActivity.class));
                break;
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(AnimalsExpectingToCalveActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(AnimalsExpectingToCalveActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(AnimalsExpectingToCalveActivity.this,
                    new String[] { permission },
                    requestCode);
        } else {
            Toast.makeText(AnimalsExpectingToCalveActivity.this,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AnimalsExpectingToCalveActivity.this,
                        "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AnimalsExpectingToCalveActivity.this,
                        "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    //Response for Barcode scanner is received here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String contents = intent.getStringExtra("SCAN_RESULT");
                //Fetching data of related animal by data scanned from barcode)
                db.collection("animals").whereEqualTo("tagNumber",contents).whereEqualTo("inCalve","1").get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(AnimalsExpectingToCalveActivity.this,"Animal with barcode " + contents + " not found within the herd");
                    }
                })  .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //convert d to our animal object
                                Animal a = d.toObject(Animal.class);
                                //=(Dialog to show animal data)
                                AnimalDialogFragment animalDialogFragment=new AnimalDialogFragment();
                                animalDialogFragment.setAnimal(a);
                                animalDialogFragment.show(getSupportFragmentManager(), AnimalDialogFragment.class.getSimpleName());
                                break;
                            }
                            if(list.size() == 0) {
                                showMessage(AnimalsExpectingToCalveActivity.this,"Animal with barcode " + contents + " not found within the herd");
                            }
                        }
                        else {
                            showMessage(AnimalsExpectingToCalveActivity.this,"Animal with barcode " + contents + " not found");
                        }
                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "RESULT_CANCELED");
            }
        }
    }

    private void showMessage(Context context, String message ) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        if (message.equals("")) {
            message = "No error message has received from server.";
        }
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
