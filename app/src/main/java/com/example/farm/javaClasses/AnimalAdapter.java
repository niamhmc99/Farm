package com.example.farm.javaClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.farm.R;
import com.example.farm.models.Animal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AnimalAdapter extends FirestoreRecyclerAdapter<Animal, AnimalAdapter.AnimalHolder> {

    public AnimalAdapter(@NonNull FirestoreRecyclerOptions<Animal> options) {
        super(options);
    }

    class AnimalHolder extends RecyclerView.ViewHolder{
        TextView textViewTagNumber, textViewAnimalName, textViewDob, textViewBreed;


        public AnimalHolder(@NonNull View itemView) {
            super(itemView);
            textViewTagNumber = itemView.findViewById(R.id.textViewTagNumber);
            textViewAnimalName = itemView.findViewById(R.id.textViewAnimalName);
            textViewDob = itemView.findViewById(R.id.textViewDob);
            textViewBreed = itemView.findViewById(R.id.textViewBreed);
        }
    }
    @Override
    protected void onBindViewHolder(@NonNull AnimalHolder animalHolder, int i, @NonNull Animal animal) {
        animalHolder.textViewTagNumber.setText(animal.getTagNumber());
        animalHolder.textViewAnimalName.setText(animal.getAnimalName());
        animalHolder.textViewDob.setText(animal.getDob());
        animalHolder.textViewBreed.setText(animal.getBreed());
    }

    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout_cardview, parent, false);
        AnimalHolder vh = new AnimalHolder(v);
        return vh;
    }





}
