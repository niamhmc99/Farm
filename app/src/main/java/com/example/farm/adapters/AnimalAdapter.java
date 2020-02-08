package com.example.farm.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farm.R;
import com.example.farm.models.Animal;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class AnimalAdapter extends FirestoreRecyclerAdapter<Animal, AnimalAdapter.AnimalHolder> {
    private OnItemClickListener listener;


    public AnimalAdapter(@NonNull FirestoreRecyclerOptions<Animal> options) {
        super(options);
    }

    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout_cardview, parent, false);
        return new AnimalHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AnimalHolder animalHolder, int i, @NonNull final Animal animal) {
        animalHolder.textViewTagNumber.setText(animal.getTagNumber());
        animalHolder.textViewAnimalName.setText(animal.getAnimalName());
        animalHolder.textViewDob.setText(animal.getDob());
        animalHolder.textViewBreed.setText(animal.getBreed());

        String imageUrl= animal.getAnimalProfilePic();

        Glide.with(animalHolder.animalProfilePic.getContext())
                .load(imageUrl)
                .into(animalHolder.animalProfilePic);
    }



    class AnimalHolder extends RecyclerView.ViewHolder{
        TextView textViewTagNumber, textViewAnimalName, textViewDob, textViewBreed;
        CircleImageView animalProfilePic;


        AnimalHolder(@NonNull View itemView) {
            super(itemView);

            textViewTagNumber = itemView.findViewById(R.id.textViewTagNumber);
            textViewAnimalName = itemView.findViewById(R.id.textViewAnimalName);
            textViewDob = itemView.findViewById(R.id.textViewDob);
            textViewBreed = itemView.findViewById(R.id.textViewBreed);
            animalProfilePic = itemView.findViewById(R.id.imageAnimalProfile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener !=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }


    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }
}
