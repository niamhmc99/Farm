package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Appointment;
import com.example.farm.adapters.VetAppointmentAdapter;
import com.example.farm.fragments.DatePickerFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class VetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, FirebaseAuth.AuthStateListener, VetAppointmentAdapter.AppointmentListener, BottomNavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "VetActivity";
    ImageView imageViewCalendarDateButton;
    TextView textViewDate;
    FloatingActionButton fabAdd;

    RecyclerView recyclerView;
    private List<Appointment> appointmentList = new ArrayList<Appointment>();
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    private CollectionReference vetRef = firestoreDB.collection("VetAppointments");
    BottomNavigationView bottomNavigationView;
    private EditText editTextAppTitle, editTextAppDesc;
    private CoordinatorLayout coordinatorLayout;

    VetAppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        firestoreDB = FirebaseFirestore.getInstance();

        imageViewCalendarDateButton = findViewById(R.id.imageViewButtonCalendarDatePicker);
        imageViewCalendarDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        textViewDate= findViewById(R.id.textViewDate);
        editTextAppTitle = findViewById(R.id.appointmentTitle);
        editTextAppDesc = findViewById(R.id.appointmentDescription);
        fabAdd = findViewById(R.id.fabAddApp);

//        appointmentList = new ArrayList<>();
//        firestoreDB.collection("VetAppointments").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                //convert d to our animal object
//                                Appointment app = d.toObject(Appointment.class);
//                                appointmentList.add(app);
//                            }
//                        }
//                    }
//                });
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.ic_vetApp);
        bottomNavigationView.setOnNavigationItemSelectedListener(VetActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(VetActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(VetActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(VetActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(VetActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(VetActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    private void setUpRecyclerView(FirebaseUser user) {

       // final String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        Query query = vetRef.whereEqualTo("userId",user.getUid())
                .orderBy("appDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Appointment> options= new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();

        appointmentAdapter = new VetAppointmentAdapter(options, this);

        recyclerView = findViewById(R.id.recyclerViewVetAppointment);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appointmentAdapter);
        appointmentAdapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void makeSnackBarMessage( String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    private void addVetApp(String textTitle, String textDesc, String date) {
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

    private boolean hasValidationErrors(String textTitle, String textDesc, String date){

        if (textTitle.trim().isEmpty()) {
            editTextAppTitle.setError("Vet Appointment Title is required");
            makeSnackBarMessage("Please enter Appointment Title.");
            return true;
        } else if (textDesc.trim().isEmpty()) {
            editTextAppDesc.setError("Appointment Description is Required");
            makeSnackBarMessage("Please enter Appointment Description.");
            return true;
        }else if (date.trim().isEmpty()) {
            textViewDate.setError("Appointment Date is Required");
            makeSnackBarMessage("Please enter Appointment Date.");
            return true;
        } else{
            return false;
        }
    }

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
                    .addBackgroundColor(ContextCompat.getColor(VetActivity.this, R.color.blue))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    @Override
    public void handleEditAppointment(final DocumentSnapshot snapshot) {
        final Appointment appointment = snapshot.toObject(Appointment.class);
        final EditText appointmentEditTextTitle = new EditText(this);
        final EditText appointmentEditTextDescription= new EditText(this);
        final TextView appointmentTextViewDate= new EditText(this);

        appointmentEditTextTitle.setText(appointment.getAppTitle());
        appointmentEditTextTitle.setSelection(appointment.getAppTitle().length());
        appointmentEditTextDescription.setText(appointment.getAppDescription());
        appointmentEditTextDescription.setSelection(appointment.getAppDescription().length());
        appointmentTextViewDate.setText(appointment.getAppDescription());
        //appointmentEditTextDate.setSelection(appointment.getAppDate().length());

        final AlertDialog show = new AlertDialog.Builder(this)
                .setTitle("Edit Appointment")
                .setView(appointmentEditTextTitle)//, appointmentEditTextDescription, appointmentTextViewDate
                .setView(appointmentEditTextDescription)
                .setView(appointmentTextViewDate)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String updateAppTitle = appointmentEditTextTitle.getText().toString();
                        String updateAppDesc = appointmentEditTextDescription.getText().toString();
                        String updateAppDate = appointmentTextViewDate.getText().toString();
//                        Date updateDate = app;
                        appointment.setAppTitle(updateAppTitle);
                        appointment.setAppDescription(updateAppDesc);
                        appointment.setAppDate(updateAppDate);
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (appointmentAdapter != null) {
            appointmentAdapter.stopListening();
        }
    }

    public void addVetAppointment(View view) {

        String appDate= textViewDate.getText().toString();
        String appTitle = editTextAppTitle.getText().toString();
        String appDesc = editTextAppDesc.getText().toString();


        if (!hasValidationErrors(appTitle, appDesc, appDate)) {
            addVetApp(appTitle, appDesc, appDate);
            editTextAppTitle.setText("");
            editTextAppDesc.setText("");
            textViewDate.setText("");
        }
    }
}
