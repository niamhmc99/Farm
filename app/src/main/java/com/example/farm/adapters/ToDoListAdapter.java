package com.example.farm.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.R;
import com.example.farm.models.Task;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ToDoListAdapter extends FirestoreRecyclerAdapter<Task, ToDoListAdapter.TaskViewHolder> {
    private static final String TAG = "ToDoListAdapter";
    TaskListener taskListener;

    public ToDoListAdapter(FirestoreRecyclerOptions<Task> options, TaskListener taskListener)
    {
        super(options);
        this.taskListener = taskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_row, parent, false);
        TaskViewHolder vh = new TaskViewHolder(view);
        return vh;
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskViewHolder holder, int i, @NonNull Task task) {
        holder.taskTextView.setText(task.getText());
        holder.checkBox.setChecked(task.getCompleted());
        CharSequence dateCharSeq = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", task.getCreated().toDate());
        holder.dateTextView.setText(dateCharSeq);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskTextView, dateTextView;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.taskTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    Task task = getItem(getAdapterPosition());
                    if (task.getCompleted() != isChecked) {
                        taskListener.handleCheckChanged(isChecked, snapshot);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    taskListener.handleEditTask(snapshot);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    taskListener.handleEditTask(snapshot);
                }
            });
        }
        public void deleteItem() {
            taskListener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    //implemented in activity
    public interface TaskListener {
         void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);
         void handleEditTask(DocumentSnapshot snapshot);
         void handleDeleteItem(DocumentSnapshot snapshot);
    }
}
