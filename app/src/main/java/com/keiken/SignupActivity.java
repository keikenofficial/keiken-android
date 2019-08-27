package com.keiken;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final ImageView background = findViewById(R.id.background);
        ImageView closeButton = findViewById(R.id.close_button);
        Button signupButton = findViewById(R.id.signup_button);


        background.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getApplicationContext()).radius(9).sampling(8).capture((ViewGroup)findViewById(R.id.cont)).into(background);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupActivity.super.onBackPressed();
            }
        });

     /*   signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, WelcomeActivity.class));
            }
        });*/


    }
}
