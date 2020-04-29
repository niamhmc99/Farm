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

        private static int SPLASH_SCREEN = 2800;

        Animation topAnim, bottomAnim;
        ImageView image;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash_screen);

            image = findViewById(R.id.imageViewSplashScreen);

            topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
            bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

            image.setAnimation(topAnim);

            //Calling New Activity after SPLASH_SCREEN seconds 1s = 1000
            new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                              startActivity(intent);
                                              finish();
                                          }
                                      }, SPLASH_SCREEN);
        }
    }