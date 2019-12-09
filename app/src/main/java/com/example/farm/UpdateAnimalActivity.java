package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateAnimalActivity extends AppCompatActivity {
    private final String  TAG= "UpdateAnimalActivity";
    private EditText editTextTagNumberUpdate, editTextAnimalNameUpdate, editTextDobUpdate, editTextSexUpdate, editTextDamUpdate, editTextCalvingDifficultyUpdate, editTextSireUpdate, editTextAiORstockbullUpdate, editTextBreedUpdate;
    private Button buttonUpdate;
    private String docId;
    private Animal animal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View mParentLayout;
    private String recievedTagNumber;
    private List<Animal> animalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_animal);

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


        animal=(Animal) getIntent().getSerializableExtra("animal");

        // initialize
        editTextTagNumberUpdate = findViewById(R.id.editTextTagNumber);
        editTextAnimalNameUpdate= findViewById(R.id.editTextName);
        editTextDamUpdate = findViewById(R.id.editTextDAM);
        editTextDobUpdate = findViewById(R.id.editTextDob);
        editTextSexUpdate = findViewById(R.id.editTextSex);
        editTextCalvingDifficultyUpdate = findViewById(R.id.editTextCalvingDifficulty);
        editTextSireUpdate =findViewById(R.id.ediTextSire);
        editTextBreedUpdate =findViewById(R.id.editTextBreed);
        editTextAiORstockbullUpdate =findViewById(R.id.editTextAIBull);

        buttonUpdate=findViewById(R.id.buttonUpdateAnimal);


        //populate animal data before update
        //was just animal before queriedAnimal*************
        editTextTagNumberUpdate.setText(animal.getTagNumber());
        editTextAnimalNameUpdate.setText(animal.getAnimalName());
        editTextDobUpdate.setText(animal.getDob());
        editTextSexUpdate.setText(animal.getSex());
        editTextDamUpdate.setText(animal.getDam());
        editTextCalvingDifficultyUpdate.setText(animal.getCalvingDifficulty());
        editTextSireUpdate.setText(animal.getSire());
        editTextBreedUpdate.setText(animal.getBreed());
        editTextAiORstockbullUpdate.setText(animal.getAiORstockbull());


    }

    private boolean hasValidationErrors(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire){
        if (tagNumber.trim().isEmpty()) {
            editTextTagNumberUpdate.setError("TagNumber is required");
            //makeSnackBarMessage("Please insert Tag Number.");
            return true;
        } else if (animalName.isEmpty()) {
            editTextAnimalNameUpdate.setError("Animal Name Required");
            //makeSnackBarMessage("Please insert Animal Name.");
            return true;
        }else if(dob.isEmpty()){
            editTextDobUpdate.setError("Date of Birth is required.");
//            makeSnackBarMessage("Please insert Date of Birth.");
            return true;
        }else if(sex.isEmpty()){
            editTextSexUpdate.setError("The sex of the Animal is required");
            //makeSnackBarMessage("Please insert the Animals Sex.");
            return true;
        }else if(dam.isEmpty()){
            editTextDamUpdate.setError("The Dam of the Animal is required");
           // makeSnackBarMessage("Please insert the Animals Dam.");
            return true;
        }else if(calvingDifficulty.isEmpty()){
            editTextCalvingDifficultyUpdate.setError("Calving Difficulty is required");
            //makeSnackBarMessage("Please insert the Calving Difficulty.");
            return true;
        }else if(sire.isEmpty()){
            editTextSireUpdate.setError("The Sire of the Animal is required");
           // makeSnackBarMessage("Please insert the Animals Sire.");
            return true;
        }else if(aiORstockbull.isEmpty()){
            editTextAiORstockbullUpdate.setError("AI / Stock Bull?");
            //makeSnackBarMessage("Please insert if insemination was by AI or Stock bull.");
            return true;
        }else if(breed.isEmpty()){
            editTextBreedUpdate.setError("Breed of Animal is Required.s");
            //makeSnackBarMessage("Please insert Animal Breed.");
            return true;
        }else{
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
        String strSex = editTextSexUpdate.getText().toString().trim();
        String strDam = editTextDamUpdate.getText().toString().trim();
        String strCalvingDif = editTextCalvingDifficultyUpdate.getText().toString().trim();
        String strSire = editTextSireUpdate.getText().toString().trim();
        String strBreed = editTextBreedUpdate.getText().toString().trim();
        String strAiOrStockbull = editTextAiORstockbullUpdate.getText().toString().trim();

        if(!hasValidationErrors(strTag, strName, strDob, strSex, strBreed, strDam, strCalvingDif, strAiOrStockbull, strSire)){
            Animal a = new Animal(strTag, strName, strDob,
                                    strSex, strBreed, strDam,
                                     strCalvingDif, strAiOrStockbull, strSire);

                db.collection("Animals").document(animal.getId())
                        .update(
                                "tagNumber", a.getTagNumber(),
                                "animalName", a.getAnimalName(),
                                "dob", a.getDob(),
                                "sex", a.getSex(),
                                "dam", a.getDam(),
                                "sire", a.getSire(),
                                "aiOrStockbull", a.getAiORstockbull(),
                                "calvingDiff", a.getCalvingDifficulty(),
                                "breed", a.getBreed()
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
                                Log.d(TAG, "Failure to update animal due to: " +e.toString());
                            }
                        });
        }
    }



//this gets the whole collection
   public void geAlltAnimals(){
       db.collection("Animals")
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
}
//   .update(
//           "tagNumber", a.getTagNumber(),
//           "animalName", a.getAnimalName(),
//           "dob", a.getDob(),
//           "sex", a.getSex(),
//           "dam", a.getDam(),
//           "sire", a.getSire(),
//           "aiOrStockbull", a.getAiORstockbull(),
//           "calvingDiff", a.getCalvingDifficulty(),
//           "breed", a.getBreed()
//           )


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