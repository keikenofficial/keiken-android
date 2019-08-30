package com.keiken.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.keiken.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

public class ForgotPasswordActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        final ImageView background = findViewById(R.id.background);
        ImageView closeButton = findViewById(R.id.close_button);

        Button sendEmailButton = findViewById(R.id.send_email_button);

        final EditText email = findViewById(R.id.email);

        mAuth = FirebaseAuth.getInstance();


        background.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getApplicationContext()).radius(9).sampling(8).capture((ViewGroup)findViewById(R.id.cont)).into(background);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordActivity.super.onBackPressed();
            }
        });

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("")) {
                    mAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Email inviata.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ForgotPasswordActivity.this, LauncherActivity.class));
                                    } else
                                        Toast.makeText(ForgotPasswordActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }
}
