package com.keiken.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;

public class ViewBookingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        //GET INTENT
        final String titolo = getIntent().getStringExtra("titolo");
        String descrizione = getIntent().getStringExtra("descrizione");
        final String luogo = getIntent().getStringExtra("luogo");
        final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        final String prezzo = getIntent().getStringExtra("prezzo");
         String ore = getIntent().getStringExtra("ore");
        String minuti = getIntent().getStringExtra("minuti");
        final String posti_prenotati = getIntent().getStringExtra("posti_prenotati");
        
        String photoUri = getIntent().getStringExtra("photoUri");
        String data_prenotazione = getIntent().getStringExtra("data_prenotazione");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");
        final String nome_utente = getIntent().getStringExtra("nome_utente");
        final String foto_utente = getIntent().getStringExtra("photo_url_creatore_esperienza");
        String isAcceptedString = getIntent().getStringExtra("isAccepted");
        boolean isAccpepted = isAcceptedString.matches("true");

        LinearLayout profilo = findViewById(R.id.account);

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


        final ImageView foto_utenteIV = findViewById(R.id.profile_pic);
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

        profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewBookingActivity.this, ViewProfileActivity.class);

                //passo i parametri per la visualizzazione del profilo
                i.putExtra("ID_PROFILO", ID_CREATORE);
                i.putExtra("profile_pic", foto_utente);
                i.putExtra("name", nome_utente);

                startActivity(i);
            }
        });

        TextView descrizioneTV = findViewById(R.id.descrizione);
        descrizioneTV.setText(descrizione);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo a persona: " + prezzo + "\u20AC");

        TextView orarioTV = findViewById(R.id.orario);
        int h = Integer.parseInt(ore);
        if (h < 10)
            ore = "0" + h;
        int min = Integer.parseInt(minuti);
        if (min <10)
            minuti ="0" + min;
        orarioTV.setText(ore+":"+minuti);
        TextView posti_prenotatiTV = findViewById(R.id.posti_prenotati);
        posti_prenotatiTV.setText("Posti prenotati: "+posti_prenotati);
        TextView reviews_button = findViewById(R.id.write_review_button);
        reviews_button.setVisibility(View.GONE);
        if(isAccpepted = true) {
            reviews_button.setVisibility(View.VISIBLE);
            //ON CLICK HANDLER PER CREARE RECENSIONI.
            //è POSSIBILE SCRIVERE UNA VOOLTA SOLA LA RECENSIONE PER OGNI ESPERIENZA, NON MODIFICABILE, NON ELIMINABILE


            //DISPLAY OK ICON

        } else {
            reviews_button.setVisibility(View.GONE);

            //DISPLAY WAITING FOR APPROVAL ICON
        }


        TextView dateTV = findViewById(R.id.date);
        dateTV.setText(data_prenotazione);

        TextView user_name = findViewById(R.id.nome_utente);
        user_name.setText(nome_utente);


    }
}
