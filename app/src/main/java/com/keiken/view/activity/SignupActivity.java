package com.keiken.view.activity;

import android.content.Context;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.keiken.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String NON_NORMAL_CHARACTERS_PATTERN = "\\W|[^!@#\\$%\\^&\\*\\(\\)]";


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
        db = FirebaseFirestore.getInstance();


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

                if (verifyProfileInformations(name, surname, day, month, year, email, password, password2)) {
                    signup(name, surname, day, month, year, email, password);
                }
            }
        });
    }


    private void signup(final String name, final String surname, final String day, final String month, final String year, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success
                            Toast.makeText(SignupActivity.this, "Registrazione avvenuta con successo.\nControlla la tua email e fai click sul link di verifica dell'account.", Toast.LENGTH_LONG).show();
                            Log.d("", "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name + " " + surname).build();
                            if (user != null) {
                                user.updateProfile(profileUpdates);
                            }


                            // Create a new user with a first and last name
                            Map<String, Object> userDb = new HashMap<>();
                            userDb.put("name", name);
                            userDb.put("surname", surname);
                            userDb.put("day", day);
                            userDb.put("month", month);
                            userDb.put("year", year);
                            userDb.put("email", email);
                            userDb.put("id", user.getUid());




                            // Add a new document with a generated ID
                            db.collection("utenti")
                                    .add(userDb)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("", "Error adding document", e);
                                        }
                                    });




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


    //verifica presenza di simboli // TRUE = PRESENZA /// FALSE = ASSENZA
    public static boolean hasSymbols(String input) {
        return input.matches(NON_NORMAL_CHARACTERS_PATTERN);
    }
    public static boolean isBisestile(int anno){
        if ( anno>1800 &&
                ( (anno%400==0) ||
                        (anno%4==0 && anno%100!=0) ) )
                            return true;
        else return false;
    }

    public boolean verifyProfileInformations(String name, String surname, String day, String month, String year, String email, String password, String password2) {

        if (name.equals("") || surname.equals("") || day.equals("") || month.equals("") || year.equals("") || email.equals("") || password.equals("") || password2.equals("")) {
            Toast.makeText(this, "Tutti i campi devono essere compilati.", Toast.LENGTH_LONG).show();
            return false;
        }
        //controllo sui caratteri in ingresso
        if (hasSymbols(name) || hasSymbols(surname) || hasSymbols(day) || hasSymbols(month) || hasSymbols(year) || hasSymbols(email)) {
            Toast.makeText(this, "I campi compilati non possono contenere caratteri speciali. ", Toast.LENGTH_LONG).show();
            return false;
        }
        if(hasSymbols(password) || hasSymbols(password2)){
            Toast.makeText(this, "La password non può contenere caratteri speciali. ", Toast.LENGTH_LONG).show();
            return false;
        }
        //


        int dayInt = Integer.parseInt(day), monthInt = Integer.parseInt(month), yearInt = Integer.parseInt(year);


        if (password.length() < 6) {
            Toast.makeText(this, "La passowrd deve essere di almeno 6 caratteri.", Toast.LENGTH_LONG).show();
            return false;
        }

        if ( !password.equals(password2) ) {
            Toast.makeText(this, "Le password non corrispondono!", Toast.LENGTH_LONG).show();
            return false;
        }

        //controllo date
        if (dayInt < 1 || dayInt > 31 || monthInt < 1 || monthInt > 12 || yearInt < 1800) {
            Toast.makeText(this, "Controlla la data inserita. ", Toast.LENGTH_LONG).show();
            return false;
        }
        if(monthInt == 2){
            if(isBisestile(yearInt)){
                if(dayInt>29){
                    Toast.makeText(this, "Controlla la data inserita. ", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                if(dayInt>28){
                    Toast.makeText(this, "Controlla la data inserita. ", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        if(monthInt == 11 || monthInt == 4 || monthInt == 6 || monthInt == 9)
            if(dayInt>30){
                Toast.makeText(this, "Controlla la data inserita. ", Toast.LENGTH_LONG).show();
                return false;
            }
        //

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        if (((yearInt > currentYear)) || (yearInt == currentYear && monthInt > currentMonth)
                ||(yearInt == currentYear && monthInt == currentMonth && dayInt >= currentDay)){
            Toast.makeText(this, "La data inserita è sbagliata!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
