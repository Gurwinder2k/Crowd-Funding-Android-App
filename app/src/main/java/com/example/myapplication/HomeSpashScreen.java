package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class HomeSpashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_spash_screen);


        //this is the splash screen code
        //this intent is delayed by 3000 milliseconds, then it opens the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomeSpashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}