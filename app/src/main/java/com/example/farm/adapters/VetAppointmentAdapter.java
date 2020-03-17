package com.example.farm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.R;
import com.example.farm.models.Appointment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class VetAppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, VetAppointmentAdapter.AppointmentHolder> {
    Context context;
    AppointmentListener appointmentListener;

    public VetAppointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull VetAppointmentAdapter.AppointmentHolder holder, int position, @NonNull Appointment appointment) {
        holder.appTitle.setText(appointment.getAppTitle());
        holder.appDescription.setText(appointment.getAppDescription());
        holder.appDate.setText(appointment.getAppDate());
    }


    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.vet_appointment_item, parent, false);
        context=parent.getContext();
        return new AppointmentHolder(v);
    }

    public class AppointmentHolder extends RecyclerView.ViewHolder {

        TextView appTitle, appDescription, appDate;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);
            appTitle = itemView.findViewById(R.id.itemTitle);
            appDescription =itemView.findViewById(R.id.itemDescription);
            appDate = itemView.findViewById(R.id.itemDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    appointmentListener.handleEditAppointment(snapshot);
                }
            });
        }

        public void deleteItem() {
            appointmentListener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface AppointmentListener {
        void handleEditAppointment(DocumentSnapshot snapshot);
        void handleDeleteItem(DocumentSnapshot snapshot);
    }
}
