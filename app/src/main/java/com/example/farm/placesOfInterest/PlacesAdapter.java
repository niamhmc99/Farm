package com.example.farm.placesOfInterest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyHolder> {
    List<Place> places;
    Button website, directions;
    private Context context;

    public PlacesAdapter(List<Place> list, Context context) {
        this.places = list;
        this.context = context;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView name,location, rating;

        public MyHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameT);
            location = itemView.findViewById(R.id.locationT);
            rating = itemView.findViewById(R.id.ratingT);
            website = itemView.findViewById(R.id.websiteButton);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item,null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        Place dataModel = places.get(position);
        holder.name.setText(dataModel.getName());
        holder.location.setText(dataModel.getLocation());
        String ratingString = String.valueOf(dataModel.getRating());
        holder.rating.setText(ratingString + " / 5");


        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < places.size(); i++)
                    if(holder.name.getText().toString().equals(places.get(i).getName())){
                        String websiteName = places.get(i).getWebsite();
                        if (websiteName != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(websiteName));
                            context.startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, "Website Unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }
}

