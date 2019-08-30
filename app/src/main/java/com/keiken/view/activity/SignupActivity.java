package com.keiken.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.keiken.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final ImageView background = findViewById(R.id.background);
        ImageView closeButton = findViewById(R.id.close_button);
        final Button signupButton = findViewById(R.id.signup_button);
        final EditText nameEditText = findViewById(R.id.name);
        final EditText surnameEditText = findViewById(R.id.surname);
        final EditText dayEditText = findViewById(R.id.day);
        final EditText monthEditText = findViewById(R.id.month);
        final EditText yearEditText = findViewById(R.id.year);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText password2EditText = findViewById(R.id.password2);


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
                SignupActivity.super.onBackPressed();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String day = dayEditText.getText().toString();
                String month = monthEditText.getText().toString();
                String year = yearEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String password2 = password2EditText.getText().toString();

                if (verifySignUpInformation(name, surname, day, month, year, email, password, password2)) {
                    signup(name, surname, day, month, year, email, password);
                }
            }
        });
    }


    private void signup(String name, String surname, String day, String month, String year, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success
                            Toast.makeText(SignupActivity.this, "Registrazione avvenuta con successo.\nControlla la tua email e fai click sul link di verifica dell'account.", Toast.LENGTH_LONG).show();
                            Log.d("", "createUserWithEmail:success");

                            sendVerificationEmail();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                // after email is sent just logout the user and finish this activity
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(SignupActivity.this, LauncherActivity.class));
                                //finish();
                            } else {
                                // email not sent, so display message and restart the activity or do whatever you wish to do
                                //restart this activity
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                            }
                        }
                    });
        }
    }


    boolean verifySignUpInformation(String name, String surname, String day, String month, String year, String email, String password, String password2) {
        if (name.equals("") || surname.equals("") || day.equals("") || month.equals("") || year.equals("") || email.equals("") || password.equals("") || password2.equals("")) {
            Toast.makeText(SignupActivity.this, "Tutti i campi devono essere compilati.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(SignupActivity.this, "La passowrd deve essere di almeno 6 caratteri.", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( !password.equals(password2) ) {
            Toast.makeText(SignupActivity.this, "Le password non corrispondono!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31 || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12 || Integer.parseInt(year) < 1800) {
            Toast.makeText(SignupActivity.this, "La data inserita è sbagliata!", Toast.LENGTH_LONG).show();

            //TODO: INSERIRE CONTROLLO DATE FUTURE E GIORNI MASSIMI DEI MESI

            return false;
        }
        return true;
    }
}
