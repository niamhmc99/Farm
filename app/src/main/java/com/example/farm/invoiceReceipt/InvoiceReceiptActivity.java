package com.example.farm.invoiceReceipt;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.farm.AnimalActivity;
import com.example.farm.LoginActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.VetActivity;
import com.example.farm.adapters.InvoiceReceiptAdapter;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.InvoiceReceipt;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import java.util.ArrayList;
import java.util.List;

public class InvoiceReceiptActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "InvoiceActivity";
    private List<InvoiceReceipt> billsList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("InvoiceReceipts");
    private InvoiceReceiptAdapter adapter;
    private FloatingActionButton mFabAdd;
    View mParentLayout;
    private Spinner spinnerInvoiceType, spinnerCategory;
    BottomNavigationView bottomNavigationView;
    private static final String KEY_INVOICERECEIPTTYPE = "invoiceReceiptType";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_USERID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_receipt);
        mFabAdd = findViewById(R.id.fabInsertInvoiceReceipt);
        mParentLayout = findViewById(android.R.id.content);
        setupFirebaseAuth();
        mFabAdd.setOnClickListener(this);

        addItemsOnSpinnerType();
        addItemsOnSpinnerCategory();
        setUpRecyclerView();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(InvoiceReceiptActivity.this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(InvoiceReceiptActivity.this, MainActivity.class);
                startActivity(intent0);
                break;

            case R.id.ic_animals:
                Intent intent1 = new Intent(InvoiceReceiptActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;

            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(InvoiceReceiptActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;

            case R.id.ic_vetApp:
                Intent intent3 = new Intent(InvoiceReceiptActivity.this, VetActivity.class);
                startActivity(intent3);
                break;

            case R.id.ic_emissions:
                Intent intent4 = new Intent(InvoiceReceiptActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }

    private void setUpRecyclerView() {
        if(adapter != null)
        {
            adapter.stopListening();
        }
        final String strUserID = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        Query query = collectionReference.orderBy("invoiceReceiptType", Query.Direction.DESCENDING)
                .whereEqualTo(KEY_INVOICERECEIPTTYPE,spinnerInvoiceType.getSelectedItem())
                .whereEqualTo(KEY_USERID, strUserID)
                .whereEqualTo(KEY_CATEGORY,spinnerCategory.getSelectedItem());

        FirestoreRecyclerOptions options= new FirestoreRecyclerOptions.Builder<InvoiceReceipt>()
                .setQuery(query, InvoiceReceipt.class)
                .build();
        adapter = new InvoiceReceiptAdapter(options);

        adapter.startListening();

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

                AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceReceiptActivity.this);
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
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabInsertInvoiceReceipt:
                startActivity(new Intent(InvoiceReceiptActivity.this, InsertInvoiceReceiptActivity.class));
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
                    Intent intent = new Intent(InvoiceReceiptActivity.this, LoginActivity.class);
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

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void addItemsOnSpinnerType(){
        spinnerInvoiceType = findViewById(R.id.spinnerType);
        List<String> list = new ArrayList<String>();
        list.add("Invoice");
        list.add("Receipt");

        ArrayAdapter<String> invoiceTypeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        invoiceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInvoiceType.setAdapter(invoiceTypeAdapter);
        spinnerInvoiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setUpRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setUpRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadData()
    {
        billsList = new ArrayList<>();
        // .whereEqualTo("invoiceType",spinnerInvoiceType.getSelectedItem())
        //                .whereEqualTo("category",spinnerCategory.getSelectedItem()).get()
        db.collection("InvoiceReceipts")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                InvoiceReceipt a = d.toObject(InvoiceReceipt.class);
                                billsList.add(a);
                            }
                        }
                    }
                });
    }
}