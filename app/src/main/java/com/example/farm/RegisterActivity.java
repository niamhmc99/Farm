package com.example.farm;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private final String  TAG= "RegisterActivitys";

    public EditText emailId, password, herdid, confirmPassword;
    Button buttonSignUp;
    TextView textViewSignIn;
    FirebaseAuth auth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        herdid = findViewById(R.id.editTextHerdid);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);
        textViewSignIn = findViewById(R.id.textViewLogin);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String confirmpass = confirmPassword.getText().toString().trim();
                String herd = herdid.getText().toString().trim();

                if(email.isEmpty()){
                    emailId.setError("Please Enter Email Address");
                    emailId.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailId.setError("Please Enter a valid email address");
                    emailId.requestFocus();
                }
                else if (email.isEmpty() && checkPassword(pass, confirmpass)){
                    Toast.makeText(RegisterActivity.this, "Please Enter an Email and Password to Register", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && checkPassword(pass, confirmpass))){
                    Log.d("", "createAccount:" + email);
                    auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "SignUp UnSuccessful, Please Try Again ", Toast.LENGTH_LONG).show();
                            }else{
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                saveUser();
                                FirebaseUser user = auth.getCurrentUser();

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        }


                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean checkPassword(String password, String confirmPassword){
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please enter your password and confirm it", Toast.LENGTH_SHORT ).show();
            return false;
        }else if(password.length() < 6 || confirmPassword.length() < 6){
            Toast.makeText(RegisterActivity.this, "Password must contain at least 6 Characters", Toast.LENGTH_SHORT ).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Both password fields must be identical ", Toast.LENGTH_SHORT ).show();
            return false;
        } else {
            return true;
        }
    }

    public void saveUser(){
        String email = emailId.getText().toString();
        String pass = password.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put(KEY_EMAIL, email);
        user.put(KEY_PASSWORD, pass);

        db.collection("Users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "User added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user", e);
                    }
                });
    }



//    *****SEND EMAIL VERIFICATION
//     mFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//        @Override
//        public void onComplete(@NonNull Task<Void> task) {
//            if(task.isSuccessful())
//                Toast.makeText(getContext(), "Email Verfication Sent", Toast.LENGTH_LONG).show();
//            else {
//                try{
//                    throw task.getException();
//                }catch (Exception e) {
//
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//    });


}


