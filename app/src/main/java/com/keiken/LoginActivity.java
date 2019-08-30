package com.keiken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView background = findViewById(R.id.background);
        ImageView closeButton = findViewById(R.id.close_button);
        Button loginButton = findViewById(R.id.login_button);
        TextView forgotPasswordButton = findViewById(R.id.forgot_password_button);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passswordEditText = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();


        background.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getApplicationContext()).radius(9).sampling(8).capture((ViewGroup) findViewById(R.id.cont)).into(background);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.super.onBackPressed();
            }
        });



        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passswordEditText.getText().toString();
                if (!email.equals("") && !password.equals(""))
                    logIn(email, password);
            }
        });
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("", "signInWithEmail:success");
                                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Email non verificata. Controlla la tua email e fai click sul link di verifica dell'account.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }



}
