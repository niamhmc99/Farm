package com.example.farm.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.farm.R;
import com.example.farm.models.Animal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class CalvingDescriptionFragment extends DialogFragment {
    private View customView;
    private Context context;
    private EditText editDescription;
    private Animal animal;
    private String docId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage fireBaseStorage;

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        context=getContext();
        customView = createView(context, R.layout.fragment_dialog_calving_description,null);

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
        customView.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        customView.findViewById(R.id.btnUpdate).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                updateAnimal(editDescription.getText().toString());
            }
        });
        fireBaseStorage= FirebaseStorage.getInstance();
        editDescription = customView.findViewById(R.id.editDescription);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(customView);
        return dialog;
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

    private void updateAnimal(String description) {

        if (!hasValidationErrors(description)) {

            db.collection("animals").document(docId)
                    .update(
                            "description", description
                    )
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Calving Information Added", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private boolean hasValidationErrors(String description) {
        if (description.trim().isEmpty()) {
            editDescription.setError("Calving description is required");
            return true;
        } else {
            return false;
        }

    }

}
