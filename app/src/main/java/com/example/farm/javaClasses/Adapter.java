package com.example.farm.javaClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.AnimalActivity;
import com.example.farm.R;
import com.example.farm.UpdateAnimalActivity;
import com.example.farm.models.Animal;

import org.w3c.dom.Text;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Animal> animalList;
    private Context mContext;
    private RecyclerView mRecyclerView;




    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
// - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Animal animal = animalList.get(position);
        holder.textViewTagNumber.setText("Tag Number: " + animal.getTagNumber());
        holder.textViewAnimalName.setText("Animal Name: " + animal.getAnimalName());
        holder.textViewDOB.setText("Date of Birth: " + animal.getDob());
        holder.textViewBreed.setText("Breed: " + animal.getBreed());

        //listen to single view layout click
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Choose Option: ");
                builder.setMessage("Update Animal's Info? ");
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //go to update activity
                        goToUpdateActivity(animal.getTagNumber());

                    }
                });
                 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView textViewTagNumber, textViewAnimalName, textViewDOB, textViewBreed;

        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout= itemView;
            textViewTagNumber = itemView.findViewById(R.id.textViewTagNumber);
            textViewAnimalName = itemView.findViewById(R.id.textViewAnimalName);
            textViewDOB = itemView.findViewById(R.id.textViewDob);
            textViewBreed = itemView.findViewById(R.id.textViewBreed);
        }
    }

    public void goToUpdateActivity(String tagNumber){
        Intent intent = new Intent(mContext, UpdateAnimalActivity.class);
        intent.putExtra("Tag_Number", tagNumber);
        mContext.startActivity(intent);
    }

    // Provide the dataset to the Adapter
    public Adapter(List<Animal> myDataset, Context context, RecyclerView recyclerView){
        animalList = myDataset;
        mContext = context;
        mRecyclerView = recyclerView;
    }


}
