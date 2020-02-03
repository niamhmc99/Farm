package com.example.farm.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

public class AnimalAdapter extends FirestoreRecyclerAdapter<Animal, AnimalAdapter.AnimalHolder> {
    private Context mContext;
    private List<Animal> animalList;
    private OnItemClickListener listener;


    public AnimalAdapter(@NonNull FirestoreRecyclerOptions<Animal> options) {

        super(options);
    }


    class AnimalHolder extends RecyclerView.ViewHolder{
        TextView textViewTagNumber, textViewAnimalName, textViewDob, textViewBreed;
        CircleImageView animalProfilePic;

        public View layout;

        public AnimalHolder(@NonNull View itemView) {
            super(itemView);
            layout= itemView;
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

        public void setAnimalImage(String animalImageUrl)
        {
            animalProfilePic= itemView.findViewById(R.id.imageAnimalProfile);

//            RequestOptions placeholderOption= new RequestOptions();
//            placeholderOption.placeholder(R.drawable.animalsmall);

//            Glide.with(mContext).applyDefaultRequestOptions(placeholderOption).load(animalImage).into(animalProfilePic);
            Glide.with(mContext)
                    .load(animalImageUrl)
                    .into(animalProfilePic);
//
//            /**
//             * gets the image url from adapter and passes to Glide API to load the image
//             *
//             * @param viewHolder
//             * @param i
//             */
//            @Override
//            public void onBindViewHolder(ViewHolder viewHolder, int i) {
//            Glide.with(context).load(imageUrls.get(i).getImageUrl()).into(viewHolder.img);
//            }

        }

    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AnimalHolder animalHolder, int i, @NonNull final Animal animal) {
        animalHolder.textViewTagNumber.setText(animal.getTagNumber());
        animalHolder.textViewAnimalName.setText(animal.getAnimalName());
        animalHolder.textViewDob.setText(animal.getDob());
        animalHolder.textViewBreed.setText(animal.getBreed());

        String animalImageUrl = animal.getAnimalProfilePic();
        animalHolder.setAnimalImage(animalImageUrl);

        //Glide.with(mContext).load(animal.getAnimalProfilePic()).into(animalHolder.animalProfilePic);


       // animalHolder.setAnimalImage(animal.getAnimalProfilePic());

//        animalHolder.animalProfilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (clickListener != null) {
//
//                    clickListener.itemClick(v, position);
//                }
//             });
//            animalHolder.animalProfilePic.setTag(animalHolder);
//        }

//        //listen to single view layout click
//        animalHolder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//                builder.setTitle("Choose option");
//                builder.setMessage("Update Animals' Information?");
//                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        //go to update activity
//                        goToViewAnimalDetails(animal.getTagNumber());
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.create().show();
//            }
//        });
    }




    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout_cardview, parent, false);
        AnimalHolder vh = new AnimalHolder(v);
        return vh;
    }

    public void deleteItem(int position){
        //gets all documents then gets doc at that postion
        getSnapshots().getSnapshot(position).getReference().delete();

    }






}
