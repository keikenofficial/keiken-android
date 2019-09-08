package com.keiken.view.activity;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ViewBookingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private BackdropFrontLayerBehavior sheetBehavior, sheetBehaviorReviews, sheetBehaviorEdit;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        /* REVIEWS INIT
        TextView reviewsButton = findViewById(R.id.reviews_button);

        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);
        final BackdropFrontLayer contentLayoutReviews = findViewById(R.id.backdrop_reviews);
        final BackdropFrontLayer contentLayoutEdit = findViewById(R.id.backdrop_edit);


        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //open reviews layer

                menuButton.setIcon(getResources().getDrawable(R.drawable.points_to_cross));
                AnimatedVectorDrawable ic = (AnimatedVectorDrawable) menuButton.getIcon();
                ic.start();

                sheetBehaviorReviews.setState(BottomSheetBehavior.STATE_EXPANDED);


            }
        });
        */



        //GET INTENT
        final String titolo = getIntent().getStringExtra("titolo");
        String descrizione = getIntent().getStringExtra("descrizione");
        final String luogo = getIntent().getStringExtra("luogo");
        final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        final String prezzo = getIntent().getStringExtra("prezzo");
        final String ore = getIntent().getStringExtra("ore");
        String minuti = getIntent().getStringExtra("minuti");
        String posti_prenotati = getIntent().getStringExtra("posti_prenotati");
        String photoUri = getIntent().getStringExtra("photoUri");
        String data_prenotazione = getIntent().getStringExtra("data_prenotazione");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");
        String nome_utente = getIntent().getStringExtra("nome_utente");
        String foto_utente = getIntent().getStringExtra("photo_url_creatore_esperienza");
        String isAcceptedString = getIntent().getStringExtra("isAccepted");
        boolean isAccpepted = isAcceptedString.matches("true");

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(titolo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        //CONTROLLO SU isAccepted + show() icona relativa allo stato della prenotazione ->
        //                                                                            // x == DENIED
        //                                                                            // ? == ONGOING
        //                                                                            // v == ACCEPTED


        //TO-DO


        ////////////////////////////////////////////////////////////////////////////////////////////
        final ImageView foto = findViewById(R.id.foto);
        if(photoUri != null) {
            storageReference.child(photoUri)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'photos/profile.png'
                    new ImageController.DownloadImageFromInternet(foto).execute(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any error
                }
            });
        }


        final ImageView foto_utenteIV = findViewById(R.id.foto);
        if(foto_utente != null) {
            storageReference.child(foto_utente)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'photos/profile.png'
                    new ImageController.DownloadImageFromInternet(foto_utenteIV).execute(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any error
                }
            });
        }

        TextView descrizioneTV = findViewById(R.id.descrizione);
        descrizioneTV.setText(descrizione);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo a persona: " + prezzo + "\u20AC");

        TextView orarioTV = findViewById(R.id.orario);
        int min = Integer.parseInt(minuti);
        if (min <10)
            minuti ="0" + min;
        orarioTV.setText(ore+":"+minuti);


        TextView dateTV = findViewById(R.id.date);
        dateTV.setText(data_prenotazione);

        TextView user_name = findViewById(R.id.nome_utente);
        user_name.setText(nome_utente);


    }
}
