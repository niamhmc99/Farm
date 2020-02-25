package com.example.farm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.R;
import com.example.farm.VetActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class VetAppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, VetAppointmentAdapter.AppointmentHolder> {
Context context;
    List<Appointment> appointmentList;
    TaskListener taskListener;

    public VetAppointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
        this.appointmentList=appointmentList;
    }



    @Override
    protected void onBindViewHolder(@NonNull VetAppointmentAdapter.AppointmentHolder holder, int position, @NonNull Appointment model) {


    }


    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.vet_appointment_item, parent, false);
        context=parent.getContext();
        return new AppointmentHolder(v);
    }

    @Override
    public int getItemCount(){
        return appointmentList.size();
    }



    public class AppointmentHolder extends RecyclerView.ViewHolder {

        TextView appTitle, appDescription, appDate;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);
            appTitle = itemView.findViewById(R.id.itemTitle);
            appDescription =itemView.findViewById(R.id.itemDescription);

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
