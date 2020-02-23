package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.farm.adapters.AnimalAdapter;
import com.example.farm.barcodeScannerTagNumber.BarcodeScannerActivity;
import com.example.farm.models.Animal;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AnimalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AnimalActivity";
    private List<Animal> animalList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference animalRef = db.collection("animals");
    private AnimalAdapter adapter;
    private FloatingActionButton mFabAddAnimal, fabSearchTagBarcode;
    View mParentLayout;
    ImageButton updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal);
        updateButton = findViewById(R.id.update_button);
        fabSearchTagBarcode = findViewById(R.id.fabSearchTagBarcode);
        mFabAddAnimal = findViewById(R.id.fabInsertAnimal);
        mParentLayout = findViewById(android.R.id.content);
        setupFirebaseAuth();
        mFabAddAnimal.setOnClickListener(this);

        animalList = new ArrayList<>();
        db.collection("animals").get()
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
    }



    private void setUpRecyclerView(){
        Query query = animalRef.orderBy("tagNumber", Query.Direction.DESCENDING);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(AnimalActivity.this);
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

        db.collection("animals").get()
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AnimalActivity.this);

                builder.setTitle("Choose option");
                builder.setMessage("Update Animals' Information?");
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AnimalActivity.this, UpdateAnimalActivity.class);
                        intent.putExtra("animal", animal);
                        intent.putExtra("documentId", docId);
                         startActivity(intent);
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

//    private void goToViewAnimalDetails(){
//        Intent intent = new Intent(mContext, UpdateAnimalActivity.class);
//        // pass all the data from animal to  Update Activity.class all in one?
//        intent.putExtra("Animal", animal);
//        mContext.startActivity(intent);
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
            case R.id.fabInsertAnimal:
                startActivity(new Intent(AnimalActivity.this, InsertAnimalActivity.class));
                break;

            case R.id.update_button:
                startActivity(new Intent(AnimalActivity.this, UpdateAnimalActivity.class));
                break;
            case R.id.fabSearchTagBarcode:
                startActivity(new Intent(AnimalActivity.this, BarcodeScannerActivity.class));
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
