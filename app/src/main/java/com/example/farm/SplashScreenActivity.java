package com.example.farm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

        private static int SPLASH_SCREEN = 2500;
        private Context context;

        Animation topAnim, bottomAnim;
        ImageView image;
        TextView txtFileFarmLogo, slogan;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);

            image = findViewById(R.id.imageViewSplashScreen);
            txtFileFarmLogo = findViewById(R.id.textViewFileFarm);
            slogan = findViewById(R.id.textViewSlogan);

            //Animations
            topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
            bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

            //Set animation to elements
            image.setAnimation(topAnim);
            txtFileFarmLogo.setAnimation(bottomAnim);
            slogan.setAnimation(bottomAnim);


            //Calling New Activity after SPLASH_SCREEN seconds 1s = 1000
            new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                              startActivity(intent);
                                              finish();

                                          }
                                      }, //Pass time here
                    SPLASH_SCREEN);

        }
    }