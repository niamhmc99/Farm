package com.example.farm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public EditText emailId, passowrd;
    Button buttonSignIn;
    TextView textViewSignUp;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextEmail);
        passowrd = findViewById(R.id.editTextPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                //checking to see if the email exists in db
                if (mFirebaseUser != null) {
                   String user = mFirebaseUser.getDisplayName();
                    Toast.makeText(LoginActivity.this, user + ": is logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pass = passowrd.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please Enter Email Address");
                    emailId.requestFocus();
                }
                else if (pass.isEmpty()) {
                    passowrd.setError("Please enter your password");
                    passowrd.requestFocus();
                }
                else if (email.isEmpty() && pass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please Enter an Email and Password to Sign In", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && pass.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("", "signInWithEmail:success");
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();

                }
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            //if in login but havent signed up
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


}
