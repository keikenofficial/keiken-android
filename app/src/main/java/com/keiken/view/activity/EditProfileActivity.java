package com.keiken.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.List;

import static com.keiken.controller.ImageController.setProfilePic;

public class EditProfileActivity extends AppCompatActivity {

    private LinearLayout backgroundFrame;
    private BackdropFrontLayerBehavior sheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();




        final EditText nameEditText = findViewById(R.id.name);
        final EditText surnameEditText = findViewById(R.id.surname);
        final EditText dayEditText = findViewById(R.id.day);
        final EditText monthEditText = findViewById(R.id.month);
        final EditText yearEditText = findViewById(R.id.year);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText password2EditText = findViewById(R.id.password2);

        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);


        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        backgroundFrame = findViewById(R.id.background_frame_x);



        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int) (80 * (displayMetrics.densityDpi / 160f));
                    int bottomBarPx = (int) (56 * (displayMetrics.densityDpi / 160f));

                    int peekHeight = displayMetrics.heightPixels - viewHeight - toolbarPx - bottomBarPx;


                    sheetBehavior.setPeekHeight(peekHeight);
                    //int bottomPx = (int)( 70 * (displayMetrics.densityDpi / 160f));
                    //sheetBehaviorReviews.setPeekHeight(bottomPx);

                    int marginPx = (int) (20 * (displayMetrics.densityDpi / 160f));

                }
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        if (user!=null)
            toolbar.setTitle(user.getDisplayName());
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mAuth != null) {
            // Name, email address, and profileImageView photo Url
            emailEditText.setText(user.getEmail());

            CollectionReference yourCollRef = db.collection("utenti");
            Query query = yourCollRef.whereEqualTo("id", user.getUid());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String name, surname, day, month, year;
                        QuerySnapshot result = task.getResult();
                        try {
                            List<DocumentSnapshot> documents = result.getDocuments();
                            DocumentSnapshot document = documents.get(0);
                            name=document.getString("name");
                            surname=document.getString("surname");
                            if(name != null &&surname != null) {
                                nameEditText.setText(name);
                                surnameEditText.setText(surname);
                            }

                            day=document.getString("day");
                            month=document.getString("month");
                            year=document.getString("year");
                            if(day != null && month != null && year != null) {
                                dayEditText.setText(day);
                                monthEditText.setText(month);
                                yearEditText.setText(year);
                            }
                        }
                        catch (Exception e) {};

                    }
                }
            });

        }



    }
}
