package com.example.farm.adapters;


import android.content.Context;
import android.text.format.DateUtils;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AnimalAdapter extends FirestoreRecyclerAdapter<Animal, AnimalAdapter.AnimalHolder> {
    private OnItemClickListener listener;

    Context context;


    public AnimalAdapter(@NonNull FirestoreRecyclerOptions<Animal> options) {
        super(options);
    }

    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.animal_rows, parent, false);
        context=parent.getContext();
        return new AnimalHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AnimalHolder animalHolder, int i, @NonNull final Animal animal) {
        animalHolder.textViewTagNumber.setText(animal.getTagNumber());
        animalHolder.textViewAnimalName.setText(animal.getAnimalName());
        animalHolder.textViewDob.setText(animal.getDob());
        animalHolder.textViewBreed.setText(animal.getBreed());

//        String timeAdded = (String) DateUtils.getRelativeTimeSpanString(animal
//                .getTimeAdded()
//                .getSeconds() * 1000);
//        animalHolder.animalRegisterTimestamp.setText(timeAdded);



        String animalImageUrl = animal.getAnimalProfilePic();
        animalHolder.setAnimalImage(animalImageUrl);
        //Glide.with(mContext).load(animal.getAnimalProfilePic()).into(animalHolder.animalProfilePic);


        // animalHolder.setAnimalImage(animal.getAnimalProfilePic());

//        String imageUrl= animal.getAnimalProfilePic();
//
//        Glide.with(animalHolder.animalProfilePic.getContext())
//                .load(imageUrl)
//                .into(animalHolder.animalProfilePic);

    }

//    public void setAnimalImage(String downloadUri, String thumbUri){
//
//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.placeholder(R.drawable.animalsmall);
//
//        Glide.with(context).applyDefaultRequestOptions(requestOptions)
//                .load(downloadUri)
//                .thumbnail(Glide.with(context).load(thumbUri))
//                .into(animalProfilePic);
//
//}



    class AnimalHolder extends RecyclerView.ViewHolder{
        TextView textViewTagNumber, textViewAnimalName, textViewDob, textViewBreed, animalRegisterTimestamp;
        CircleImageView animalProfilePic;

        AnimalHolder(@NonNull View itemView) {
            super(itemView);

            textViewTagNumber = itemView.findViewById(R.id.textViewTagNumber);
            textViewAnimalName = itemView.findViewById(R.id.textViewAnimalName);
            textViewDob = itemView.findViewById(R.id.textViewDob);
            textViewBreed = itemView.findViewById(R.id.textViewBreed);
            animalProfilePic = itemView.findViewById(R.id.imageAnimalProfile);
            animalRegisterTimestamp = itemView.findViewById(R.id.animalRegisterTimestamp);

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
              animalProfilePic = itemView.findViewById(R.id.imageAnimalProfile);

            RequestOptions placeholderOption= new RequestOptions();
            placeholderOption.placeholder(R.drawable.animalsmall);

            //Preconditions.checkNotNull(mContext); -- this is throwing null pointer exception
            if (context!= null) {
                Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(animalImageUrl).into(animalProfilePic);
            }
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
