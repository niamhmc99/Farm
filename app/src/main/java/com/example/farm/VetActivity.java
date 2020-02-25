package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.adapters.Appointment;
import com.example.farm.adapters.VetAppointmentAdapter;
import com.example.farm.fragments.DatePickerFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class VetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, FirebaseAuth.AuthStateListener, VetAppointmentAdapter.TaskListener{
    private static final String TAG = "AnimalActivity";
    ImageView imageViewCalendarDateButton;
    TextView textViewDate;
    FloatingActionButton fabAdd;

    RecyclerView recyclerView;
    private List<Appointment> appointmentList = new ArrayList<Appointment>();
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private CollectionReference animalRef = firestoreDB.collection("VetAppointments");
    public boolean isUpdate;
    public String updateId = "";
    public MaterialEditText editTextAppTitle;
    public MaterialEditText editTextAppDesc;
    public Date editTextAppDate;

    VetAppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet);
        firestoreDB = FirebaseFirestore.getInstance();

        imageViewCalendarDateButton = findViewById(R.id.imageViewButtonCalendarDatePicker);
        imageViewCalendarDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        editTextAppTitle = findViewById(R.id.appointmentTitle);
        editTextAppDesc = findViewById(R.id.appointmentDescription);

        editTextAppTitle.getText().toString();
        editTextAppDesc.getText().toString();
        //editTextAppDate.getDate();

        fabAdd = findViewById(R.id.fabAddApp);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVetApp(editTextAppTitle, editTextAppDesc, editTextAppDate);
            }
        });

        appointmentList = new ArrayList<>();
        firestoreDB.collection("VetAppointments").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //convert d to our animal object
                                Appointment app = d.toObject(Appointment.class);
                                appointmentList.add(app);
                            }
                        }
                    }
                });
    }

    private void setUpRecyclerView(FirebaseUser user) {

        Query query = animalRef.orderBy("tagNumber", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Appointment> options= new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();
        appointmentAdapter = new VetAppointmentAdapter(options);

        recyclerView = findViewById(R.id.recyclerViewVetAppointment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appointmentAdapter);
        appointmentAdapter.startListening();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addVetApp(MaterialEditText textTitle, MaterialEditText textDesc, Date date) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Appointment appointment = new Appointment(textTitle, textDesc, userId, date);

        FirebaseFirestore.getInstance()
                .collection("VetAppointments")
                .add(appointment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Succesfully added the scheduled vet appointment...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                        Toast.makeText(VetActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //for the recycler view
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Toast.makeText(VetActivity.this, "Deleting Task", Toast.LENGTH_SHORT).show();
                //delete item if swiped left
                VetAppointmentAdapter.AppointmentHolder appointmentHolder = (VetAppointmentAdapter.AppointmentHolder) viewHolder;
                appointmentHolder.deleteItem();
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(VetActivity.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    //interface method
    @Override
    public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot) {
        Log.d(TAG, "handleCheckChanged: " + isChecked);
        snapshot.getReference().update("completed", isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void handleEditTask(final DocumentSnapshot snapshot) {
        final Appointment appointment = snapshot.toObject(Appointment.class);
        final EditText appointmentEditTextTitle = new EditText(this);
        final EditText appointmentEditTextDescription= new EditText(this);
        final EditText appointmentEditTextDate= new EditText(this);

        appointmentEditTextTitle.setText(appointment.getAppTitle());
        appointmentEditTextTitle.setSelection(appointment.getAppTitle().length());
        appointmentEditTextDescription.setText(appointment.getAppDescription());
        appointmentEditTextDescription.setSelection(appointment.getAppDescription().length());
        appointmentEditTextDate.setText(appointment.getAppDescription());
//        appointmentEditTextDate.setSelection(appointment.getAppDate());

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(appointmentEditTextDescription)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String updateAppTitle = appointmentEditTextTitle.getText().toString();
                        String updateAppDesc = appointmentEditTextDescription.getText().toString();
//                        Date updateDate = app
//                        appointment.setText(newText);
                        snapshot.getReference().set(appointment)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: ");
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void handleDeleteItem(DocumentSnapshot snapshot) {
        final DocumentReference documentReference = snapshot.getReference();
        final Appointment appointment= snapshot.toObject(Appointment.class);

        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Item deleted");
                    }
                });

        Snackbar.make(recyclerView, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        documentReference.set(appointment);
                    }
                })
                .show();
    }



//
//    private void getAppointments() {
//        final AlertDialog dialog = new SpotsDialog.Builder()
//                .setContext(this)
//                .setMessage("Scheduled Vet Appointments")
//                .setCancelable(false)
//                .build();
//
//        dialog.show();
//
//        firestoreDB.collection("VetAppointments")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                for (DocumentSnapshot documentSnapshot : task.getResult()) {
//
//                    Appointment appointment = new Appointment(documentSnapshot.getString("id"),
//                            documentSnapshot.getString("appointmentTitle"),
//                            documentSnapshot.getString("appointmentDescription"),
//                            documentSnapshot.getDate("appointmentDate"));
//                    appointmentList.add(appointment);
//
//                }
//                recyclerView.setAdapter(appointmentAdapter);
//                dialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(VetActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumainopts, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VetActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VetActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDatestring = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(currentDatestring);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            setUpRecyclerView(firebaseAuth.getCurrentUser());

        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
            Intent intent = new Intent(VetActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }
}
