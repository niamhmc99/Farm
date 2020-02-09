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

    Context context;


    public AnimalAdapter(@NonNull FirestoreRecyclerOptions<Animal> options) {
        super(options);
    }

    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout_cardview, parent, false);
        context=parent.getContext();
        return new AnimalHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull AnimalHolder animalHolder, int i, @NonNull final Animal animal) {
        animalHolder.textViewTagNumber.setText(animal.getTagNumber());
        animalHolder.textViewAnimalName.setText(animal.getAnimalName());
        animalHolder.textViewDob.setText(animal.getDob());
        animalHolder.textViewBreed.setText(animal.getBreed());



        String animalImageUrl = animal.getAnimalProfilePic();
        animalHolder.setAnimalImage(animalImageUrl);
        System.out.println(animalImageUrl+"((((((((((((((((((((((((");

        //Glide.with(mContext).load(animal.getAnimalProfilePic()).into(animalHolder.animalProfilePic);


        // animalHolder.setAnimalImage(animal.getAnimalProfilePic());

//        String imageUrl= animal.getAnimalProfilePic();
//
//        Glide.with(animalHolder.animalProfilePic.getContext())
//                .load(imageUrl)
//                .into(animalHolder.animalProfilePic);


//        Glide.with(animalHolder.textViewBreed.getContext())
//                .load("https://firebasestorage.googleapis.com/v0/b/farm-3b20e.appspot.com/o/profile_images%2F2df2984e-9c44-4c42-acc2-61c5175a1cf9.jpg?alt=media&token=3f69f62f-2f55-4461-904a-87e5c6b2e4f9")
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
