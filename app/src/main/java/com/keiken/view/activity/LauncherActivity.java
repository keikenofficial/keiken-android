package com.keiken.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.keiken.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        final Button signupButton = findViewById(R.id.signup_button);
        final Button loginButton = findViewById(R.id.login_button);
        final Button googleButton = findViewById(R.id.google_button);
        final Button facebookButton = findViewById(R.id.facebook_button);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        FacebookSdk.setApplicationId("419212508697049");
        AppEventsLogger.activateApp(getApplication());

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast t = Toast.makeText(LauncherActivity.this, "Cancellazione Login.", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 160);
                        t.show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast t = Toast.makeText(LauncherActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 160);
                        t.show();
                    }
                });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LauncherActivity.this, SignupActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LauncherActivity.this, Arrays.asList("public_profile", "email"));
            }
        });


    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");


                            String name = acct.getGivenName();
                            String surname = acct.getFamilyName();

                            uploadUserToDb(name, surname);
                            startActivity(new Intent(getBaseContext(), HomeActivity.class));


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LauncherActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    } //called by onActivityResult for Google auth


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase (auth with google credential)
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e);
                // ...
            }
        } else
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");


                            Profile profile= Profile.getCurrentProfile();
                            final String name = profile.getName();
                            final String surname = profile.getLastName();


                            uploadUserToDb(name, surname);

                            startActivity(new Intent(LauncherActivity.this, HomeActivity.class));



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast t = Toast.makeText(LauncherActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG);
                            t.setGravity(Gravity.TOP, 0, 160);
                            t.show();
                        }

                    }
                });
    } //called by LoginManager callback for Facebook auth



    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            boolean externalProvider = false;
            for (UserInfo user : (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())).getProviderData()) {
                if (user.getProviderId().equals("facebook.com") || user.getProviderId().equals("google.com")) {
                    externalProvider = true;
                    startActivity(new Intent(LauncherActivity.this, HomeActivity.class));

                }
            }
            if (!externalProvider && currentUser.isEmailVerified())
                startActivity(new Intent(LauncherActivity.this, HomeActivity.class));
        }
    }



    private void uploadUserToDb(final String name, final String surname){


        //checks firestore database in order to see if user already exists, if so, do nothing
        CollectionReference utenti = db.collection("utenti");
        Query query = utenti.whereEqualTo("id", mAuth.getCurrentUser().getUid());
        Task<QuerySnapshot> querySnapshotTask = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {



                        // Create a new user with a first and last name
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, Object> userDb = new HashMap<>();


                        userDb.put("name", name);
                        userDb.put("surname", surname);
                        userDb.put("email", user.getEmail());
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




                    }
                }

            }
        });



    }
}
