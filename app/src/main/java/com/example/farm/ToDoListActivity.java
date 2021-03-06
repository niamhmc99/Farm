package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.farm.adapters.ToDoListAdapter;
import com.example.farm.emissions.EmissionsActivity;
import com.example.farm.googlemaps.MapsActivity;
import com.example.farm.models.Task;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import java.util.Date;

public class ToDoListActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, ToDoListAdapter.TaskListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "ToDoListActivity";
    private ToDoListAdapter toDoListAdapter;
    RecyclerView recyclerView;
    private FloatingActionButton mFabToDo;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        recyclerView = findViewById(R.id.recyclerViewToDo);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mFabToDo = findViewById(R.id.fabToDo);
        mFabToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(ToDoListActivity.this);
    }
    private void showAlertDialog() {
        final EditText taskEditText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add To Do Task")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "onClick: " + taskEditText.getText());
                        addToDoTask(taskEditText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addToDoTask(String text) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task task = new Task(text, false, new Timestamp(new Date()), userId);

        FirebaseFirestore.getInstance()
                .collection("Tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: Succesfully added the task...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getLocalizedMessage() );
                        Toast.makeText(ToDoListActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ic_home:
                Intent intent0 = new Intent(ToDoListActivity.this, MainActivity.class);
                startActivity(intent0);
                break;
            case R.id.ic_animals:
                Intent intent1 = new Intent(ToDoListActivity.this, AnimalActivity.class);
                startActivity(intent1);
                break;
            case R.id.ic_nearbyPlaces:
                Intent intent2 = new Intent(ToDoListActivity.this, MapsActivity.class);
                startActivity(intent2);
                break;
            case R.id.ic_vetApp:
                Intent intent3 = new Intent(ToDoListActivity.this, VetActivity.class);
                startActivity(intent3);
                break;
            case R.id.ic_emissions:
                Intent intent4 = new Intent(ToDoListActivity.this, EmissionsActivity.class);
                startActivity(intent4);
                break;
        }
        return true;
    }


    private void setUpRecyclerView(FirebaseUser user) {
            Query query = FirebaseFirestore.getInstance()
                    .collection("Tasks")
                    .whereEqualTo("userId", user.getUid())
                    .orderBy("completed", Query.Direction.ASCENDING)
                    .orderBy("created", Query.Direction.DESCENDING);
            FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                    .setQuery(query, Task.class)
                    .build();
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            toDoListAdapter = new ToDoListAdapter(options, this);
            recyclerView.setAdapter(toDoListAdapter);
            toDoListAdapter.startListening();
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            setUpRecyclerView(firebaseAuth.getCurrentUser());
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
            Intent intent = new Intent(ToDoListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
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
                Toast.makeText(ToDoListActivity.this, "Deleting Task", Toast.LENGTH_SHORT).show();
                ToDoListAdapter.TaskViewHolder taskViewHolder = (ToDoListAdapter.TaskViewHolder) viewHolder;
                taskViewHolder.deleteItem();
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ToDoListActivity.this, R.color.blue))
                    .addActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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
        final Task task = snapshot.toObject(Task.class);
        final EditText taskEditText = new EditText(this);
        taskEditText.setText(task.getText());
        taskEditText.setSelection(task.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(taskEditText)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newText = taskEditText.getText().toString();
                        task.setText(newText);
                        snapshot.getReference().set(task)
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
        final Task task = snapshot.toObject(Task.class);
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
                        documentReference.set(task);
                    }
                }).show();
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
        if (toDoListAdapter != null) {
            toDoListAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toDoListAdapter != null) {
            toDoListAdapter.notifyDataSetChanged();
        }
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
                startActivity(new Intent(ToDoListActivity.this, LoginActivity.class));
            case R.id.menuHome:
                Toast.makeText(this, "Home Page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ToDoListActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}
