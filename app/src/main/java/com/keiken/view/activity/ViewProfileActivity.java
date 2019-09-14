package com.keiken.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;
import com.keiken.model.Esperienza;
import com.keiken.view.RVAdapterProfile;
import com.keiken.view.RecyclerViewHeader;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ViewProfileActivity extends AppCompatActivity {


    private BackdropFrontLayerBehavior sheetBehavior;
    private int peekHeight = 0;
    private MaterialButton menuButton;
    private ImageView upArrow;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);


        mAuth=FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        TextView reviewsButton = findViewById(R.id.reviews_button);


        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);

        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


        menuButton = findViewById(R.id.menu_button);
        upArrow = findViewById(R.id.up_arrow);


        contentLayout.setBehavior(sheetBehavior);
        contentLayout.setButton(menuButton);
        contentLayout.setDrawable((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.cross_to_points));

        contentLayout.setImageView(upArrow);
        contentLayout.setDrawable2((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.black_to_white_up_arrow));




        contentLayout.setBehavior(sheetBehavior);



        final DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        final LinearLayout backgroundFrame = findViewById(R.id.background_frame);


        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int)( 80 * (displayMetrics.densityDpi / 160f));

                    peekHeight = displayMetrics.heightPixels-viewHeight-toolbarPx;

                    sheetBehavior.setPeekHeight(peekHeight);

                }
            });
        }






        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    //  recursiveLoopChildren(false, contentLayout);
                    menuButton.setIcon(getResources().getDrawable(R.drawable.points_to_cross));
                    AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)menuButton.getIcon();
                    ic.start();

                    upArrow.setImageDrawable((getResources().getDrawable(R.drawable.white_to_black_up_arrow)));
                    AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
                    ic2.start();

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else {

                    menuButton.setIcon(getResources().getDrawable(R.drawable.cross_to_points));
                    AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)menuButton.getIcon();
                    ic.start();

                    upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
                    AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
                    ic2.start();

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    // recursiveLoopChildren(true, contentLayout);

                }
            }
        });

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });



        //Importo i dati
        String ID_PROFILO = getIntent().getStringExtra("ID_PROFILO");
        String profile_pic = getIntent().getStringExtra("profile_pic");
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String email = getIntent().getStringExtra("email");
        String bio = getIntent().getStringExtra("bio");
        String day = getIntent().getStringExtra("day");
        String month = getIntent().getStringExtra("month");
        String year = getIntent().getStringExtra("year");

        boolean publicSurname  = getIntent().getBooleanExtra("publicSurname", false);
        boolean publicEmail = getIntent().getBooleanExtra("publicEmail", false);
        boolean publicDate = getIntent().getBooleanExtra("publicDate", false);





        //TextView nameTV = findViewById(R.id.user_name);
        final ImageView profile_picIV = findViewById(R.id.profile_pic);


        //nameTV.setText(name+" "+surname);
        if (publicSurname) toolbar.setTitle(name + " " + surname);
        else toolbar.setTitle(name);


        TextView bioTV = findViewById(R.id.bio);
        if (bio != null && !bio.equals("")) {
            bioTV.setText(bio);
            bioTV.setVisibility(View.VISIBLE);
        }


        TextView emailTv = findViewById(R.id.email);
        if (publicEmail) emailTv.setText(email);
        else emailTv.setVisibility(View.GONE);
        TextView dateTv = findViewById(R.id.date);
        if (publicDate) dateTv.setText(day + "/" + month + "/" + year);
        else dateTv.setVisibility(View.GONE);



        if (profile_pic != null) {
            storageReference.child(profile_pic)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'photos/profile.png'
                    new ImageController.DownloadImageFromInternet(profile_picIV).execute(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any error
                }
            });
        }



        //init recycleView
        final RecyclerViewHeader headerRV = findViewById(R.id.rvHeader);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        final RecyclerView rv = findViewById(R.id.esperienze);
        rv.setLayoutManager(llm);
        headerRV.attachTo(rv);


        rv.setFocusable(false);
        rv.setHasFixedSize(true);
        //Query per mostrare le esperienze
            final CollectionReference esperienze = db.collection("esperienze");
            Query query = esperienze.whereEqualTo("ID_CREATORE", ID_PROFILO);
            Task<QuerySnapshot> querySnapshotTask = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        final ArrayList<Esperienza> esperienze = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //inizializzazione dati con valori presi dal DB
                            if (document.exists()) {


                                final String titolo = (String) document.get("titolo");
                                final String descrizione = (String) document.get("descrizione");
                                final String luogo = (String) document.get("luogo");
                                final String ID_CREATORE = (String) document.get("ID_CREATORE");
                                final String prezzo = (String) document.get("prezzo");
                                final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document.get("categorie"));
                                final String ID_ESPERIENZA = (String) document.getId();
                                final Long ore = (Long) document.get("ore");
                                final Long minuti = (Long) document.get("minuti");
                                final Long nPostiDisponibili = (Long) document.get("posti_massimi");
                                final String photoUri = (String) document.get("photoUri");
                                db.collection("esperienze").document(document.getId()).collection("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Esperienza e;
                                            HashMap<Date, Long> date = new HashMap<Date, Long>();
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.exists()) {
                                                    //Long tempTimestamp = (Long) ((HashMap<String, Object>) document.get("data")).get("timeInMillis");
                                                    //Calendar tempCalendar = new GregorianCalendar();
                                                    //tempCalendar.setTimeInMillis(tempTimestamp);
                                                    Timestamp timestamp = (Timestamp) document.get("data");
                                                    Date data = timestamp.toDate();
                                                    Long nPostiDisponibili = (Long) document.get("posti_disponibili");
                                                    date.put(data, nPostiDisponibili);
                                                } else {
                                                    Log.d("", "No such document");
                                                }
                                            }
                                            e = new Esperienza(titolo, descrizione, luogo, ID_CREATORE, prezzo, categorie, date, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                            esperienze.add(e);

                                            RVAdapterProfile adapter = new RVAdapterProfile(esperienze, new RVAdapterProfile.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Esperienza esperienza) {
                                                    Intent i = new Intent(getApplicationContext(), ViewExperienceActivity.class);
                                                    i.putExtra("titolo", esperienza.getTitolo());
                                                    i.putExtra("descrizione", esperienza.getDescrizione());
                                                    i.putExtra("luogo", esperienza.getLuogo());
                                                    i.putExtra("ID_CREATORE", esperienza.getID_CREATORE());
                                                    i.putExtra("prezzo", esperienza.getPrezzo());
                                                    i.putExtra("categorie", esperienza.getCategorie());
                                                    i.putExtra("ore", Long.toString(esperienza.getOre()));
                                                    i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                    i.putExtra("nPostiDisponibili", Long.toString(esperienza.getnPostiDisponibili()));
                                                    i.putExtra("photoUri", esperienza.getPhotoUri());
                                                    i.putExtra("date", esperienza.getDate());
                                                    i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());

                                                    startActivity(i);
                                                }
                                            });
                                            rv.setAdapter(adapter);

                                        } else {
                                            Log.d("", "get failed with ", task.getException());
                                        }
                                    }
                                });


                            }

                        }

                    }
                }

            });




    }


   /* public void recursiveLoopChildren(boolean enable, ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);

            child.setEnabled(enable);

            if (child instanceof ViewGroup) {
                recursiveLoopChildren(enable, (ViewGroup) child);
            }
        }
    }   */


    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            //action not popBackStack

            menuButton.setIcon(getResources().getDrawable(R.drawable.cross_to_points));
            AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)menuButton.getIcon();
            ic.start();

            upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
            AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
            ic2.start();

            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            super.onBackPressed();
        }
    }




}

