package com.example.farm.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farm.R;
import com.example.farm.models.Animal;

public class AnimalDialogFragment extends DialogFragment {
    private View customView;
    private Context context;
    private Animal animal;

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        context=getContext();
        customView = createView(context, R.layout.fragment_dialog_animal,null);

        final Dialog dialog=new Dialog(context, R.style.AppTheme);
        if(dialog.getWindow()!=null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(true);

        customView.setFocusableInTouchMode(true);
        customView.requestFocus();
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }
                return false;
            }
        });
        ((TextView)customView.findViewById(R.id.textViewCalvingInfo)).setText(animal.getTagNumber());
        ((TextView)customView.findViewById(R.id.textViewAnimalName)).setText(animal.getAnimalName());
        ((TextView)customView.findViewById(R.id.textViewDob)).setText(animal.getDob());
        ((TextView)customView.findViewById(R.id.textViewBreed)).setText(animal.getBreed());

        ((TextView)customView.findViewById(R.id.textViewCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        String animalImageUrl = animal.getAnimalProfilePic();
        setAnimalImage(animalImageUrl);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(customView);
        return dialog;
    }

    private void setAnimalImage(String animalImageUrl)
    {
        ImageView animalProfilePic = customView.findViewById(R.id.imageAnimalProfile);

        RequestOptions placeholderOption= new RequestOptions();

        if (context!= null) {
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(animalImageUrl).into(animalProfilePic);
        }
    }

    private static View createView(Context context, int layout, ViewGroup parent) {
        try {
            LayoutInflater newLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return newLayoutInflater.inflate(layout, parent, false);
        } catch (Exception e) {
            return null;
        }
    }
}
