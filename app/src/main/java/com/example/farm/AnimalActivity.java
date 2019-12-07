package com.example.farm;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.farm.javaClasses.Adapter;
import com.example.farm.javaClasses.AnimalAdapter;
import com.example.farm.models.Animal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AnimalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AnimalActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private String filter = "";
    //******Adapter adapter;
    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance(); //connects to DB
    //private DocumentReference animal = db.document("Animals");
    //want to add animals to db
    private CollectionReference animalRef = db.collection("Animals");
    private AnimalAdapter adapter;

    //widgets
    private FloatingActionButton mFabAdd;

    //vars
    View mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        Intent intent = getIntent();

        mFabAdd = findViewById(R.id.fab);
        mParentLayout = findViewById(android.R.id.content);
        setupFirebaseAuth();
        mFabAdd.setOnClickListener(this);

        //populate recyclerview
       // populaterecyclerView(filter);
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = animalRef.orderBy("tagNumber", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Animal> options= new FirestoreRecyclerOptions.Builder<Animal>()
                .setQuery(query, Animal.class)
                .build();
        adapter = new AnimalAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
        //Add the divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));



    }

//    public void listAnimals(String filter) {
//        ListAnimalsPage page = FirebaseAuth.getInstance().listAnimals(null);
//        while (page != null) {
//            for (ExportedAnimalRecord animal : page.getValues()) {
//                System.out.println("User: " + animal.getTagNum());
//            }
//            page = page.getNextPage();
//        }
//        page = FirebaseAuth.getInstance().listAnimals(null);
//        for (ExportedAnimalRecord animal : page.iteratAll()) {
//            System.out.println("User: " + animal.getTagNum());
//        }
//    }

//    public void listOfAnimals(){
//        db.collection("Animals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    List<String> list = new ArrayList<>();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        list.add(document.getId());
//                    }
//                    Log.d(TAG, list.toString());
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });
//    }

//    public void loadAnimal(View view){
//        animalRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot  queryDocumentSnapshots) {
//                List<DocumentReference> animals = new ArrayList<>();
//                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
//                    Animal animal = documentSnapshot.toObject(Animal.class);
//
//                    String tagNumber = animal.getTagNumber();
//                    String name = animal.getAnimalName();
//                    String breed = animal.getBreed();
//                    String dob = animal.getDob();
//                    String sex = animal.getSex();
//                    String calvingDif = animal.getCalvingDifficulty();
//                    String aiOrBull = animal.getAiORstockbull();
//                    String dam = animal.getDam();
//                    String sire = animal.getSire();
//                }
//                animals.add(animal);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
//    }


    private void populaterecyclerView(String filter){
        //adapter = new Adapter(listOfAnimals(), this, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    //method to create snack bar message
    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
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
                startActivity(new Intent(AnimalActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AnimalActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fab:{
                //add new animal
                startActivity(new Intent(AnimalActivity.this, AddAnimalActivity.class));
                break;
            }
        }

    }


    //Fire base Setup
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
                    Intent intent = new Intent(AnimalActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
//        }
        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
