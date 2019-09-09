package com.keiken.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewBookingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private ImageView foto_utenteIV;
    private ImageView foto;
    private TextView descrizioneTV;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);





        //GET INTENT
        final String titolo = getIntent().getStringExtra("titolo");
        String descrizione = getIntent().getStringExtra("descrizione");
        final String luogo = getIntent().getStringExtra("luogo");
        final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        final String ID_PRENOTANTE = getIntent().getStringExtra("ID_PRENOTANTE");
        final String prezzo = getIntent().getStringExtra("prezzo");
        String ore = getIntent().getStringExtra("ore");
        String minuti = getIntent().getStringExtra("minuti");
        final String posti_prenotati = getIntent().getStringExtra("posti_prenotati");

        String photoUri = getIntent().getStringExtra("photoUri");
        String data_prenotazione = getIntent().getStringExtra("data_prenotazione");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");
        final String nome_utente = getIntent().getStringExtra("nome_utente");
        final String foto_utente = getIntent().getStringExtra("photo_url_creatore_esperienza");
        final String foto_utente_prenotante = getIntent().getStringExtra("photo_url_prenotante_esperienza");
        boolean isAccepted = getIntent().getExtras().getBoolean("isAccepted");
        final String ID_PRENOTAZIONE = getIntent().getStringExtra("ID_PRENOTAZIONE");

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

        TextView reviews_button = findViewById(R.id.write_review_button);

        final TextView confermata_rifiutata_textview = findViewById(R.id.confermata_rifiutata_textview);
        confermata_rifiutata_textview.setVisibility(View.GONE);

        final LinearLayout conferma_rifiuta_prenotazione_layout =findViewById(R.id.conferma_rifiuta_prenotazione_layout);
        conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);

        final MaterialButton accetta_esperienza = findViewById(R.id.accetta_esperienza);
        final MaterialButton rifiuta_esperienza = findViewById(R.id.rifiuta_esperienza);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo totale: " + prezzo + "\u20AC");


        TextView orarioTV = findViewById(R.id.orario);
        int h = Integer.parseInt(ore);
        int min = Integer.parseInt(minuti);
        if (h <10)
            ore ="0" + ore;
        if (min <10)
            minuti ="0" + min;
        orarioTV.setText(ore+":"+minuti);

        TextView posti_prenotatiTV = findViewById(R.id.posti_prenotati);
        posti_prenotatiTV.setText("Posti prenotati: " + posti_prenotati);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView dateTV = findViewById(R.id.date);
        dateTV.setText(data_prenotazione);

        TextView user_name = findViewById(R.id.nome_utente);
        user_name.setText(nome_utente);

        //ON CLICK LISTENER
        ///////////////////////////////////////////// CONFERMA BUTTON
        //conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);   NON CREDO CHE SERVA
        accetta_esperienza.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                accetta_esperienza.setEnabled(false);
                rifiuta_esperienza.setEnabled(false);
                try{
                    //UPDATE VARIABILE isAccepted
                    final DocumentReference prenotazioniDb = db.collection("prenotazioni").document(ID_PRENOTAZIONE);
                    prenotazioniDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("isAccepted", true);
                                    db.collection("prenotazioni").document(document.getId()).update(map);
                                    Log.d("", "Modifica prenotazione accettata. ");

                                    conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                    //DISPLAY OK ICON
                                    confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                    confermata_rifiutata_textview.setText("Prenotazione cofermata");
                                    confermata_rifiutata_textview.setTextColor(65280);
                                }
                            }
                        }
                    });
                }catch (NullPointerException e){
                    accetta_esperienza.setEnabled(true);
                    rifiuta_esperienza.setEnabled(true);
                }
            }
        });  //Trovare il problema
/////////////////////////////////////////////////////////////////////////////////    FINE CONFERMA BUTTON


        //DA IMPLEMENTARE (?) ALLA MODIFICA/ELIMINA DEL DOCUMENTO PREVIENE EVENTUALI CRASH
        /*
        db.collection("prenotazioni").whereEqualTo("ID_PRENOTANTE", ID_PRENOTANTE)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    //Here i am getting full newly added documents

                                    break;
                                case MODIFIED:
                                    //here i am getting which is modified document,ok fine...,But i want to get only the filed which i modified instance of getting full documents..
                                    if(dc.getDocument().get("isAccepted").equals("true")) {
                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ID_CREATORE)) {
                                            //non è il creatore

                                            //DISPLAY OK ICON
                                            conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                            confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                            confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                                            confermata_rifiutata_textview.setText("La tua prenotazione è stata confermata!");

                                            conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                            //DISPLAY OK ICON
                                            confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                            confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                                        } else {

                                        }
                                    }
                                    break;
                                case REMOVED:
                                    Intent i = new Intent(ViewBookingActivity.this, HomeActivity.class);
                                    break;
                            }
                        }

                    }
                });

        db.collection("prenotazioni").whereEqualTo("ID_CREATORE_ESPERIENZA", ID_CREATORE)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    //Here i am getting full newly added documents
                                    break;
                                case MODIFIED:
                                    //here i am getting which is modified document,ok fine...,But i want to get only the filed which i modified instance of getting full documents..
                                    break;
                                case REMOVED:

                                    break;
                            }
                        }

                    }
                });
        */


        /*
        rifiuta_esperienza.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                accetta_esperienza.setEnabled(false);
                rifiuta_esperienza.setEnabled(false);

                db.collection("prenotazioni").document(ID_PRENOTAZIONE)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("", "DocumentSnapshot successfully deleted!");
                                try{
                                    //UPDATE POSTI DISPONIBILI INTERROGO IL DATABASE VIA ID_ESPERIENZA
                                    final DocumentReference esperienzeDb = db.collection("esperienze").document(ID_ESPERIENZA);
                                    esperienzeDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                            if(task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if(document.exists()){
                                                    Map<String, Object> map = new HashMap<>();
                                                    long posti_massimi = Long.parseLong((String) document.get("posti_massimi"));
                                                    long new_posti = posti_massimi + Long.parseLong(posti_prenotati);
                                                    map.put("posti_massimi", new_posti);
                                                    esperienzeDb.update(map);
                                                }
                                            }
                                        }
                                    });
                                }catch (NullPointerException e){
                                    accetta_esperienza.setEnabled(true);
                                    rifiuta_esperienza.setEnabled(true);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("", "Error deleting document", e);
                            }
                        });
            }
        });*/ // Trovare un metodo per fare un "soft_delete" della prenotazione

        if(!(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(ID_CREATORE))) {
            foto = findViewById(R.id.foto);
            if (photoUri != null) {
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


            foto_utenteIV = findViewById(R.id.profile_pic);
            if (foto_utente != null) {
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

            descrizioneTV = findViewById(R.id.descrizione);
            descrizioneTV.setText(descrizione);

            reviews_button.setVisibility(View.GONE);
            if (isAccepted) {

                //ON CLICK HANDLER PER CREARE RECENSIONI.
                //è POSSIBILE SCRIVERE UNA VOOLTA SOLA LA RECENSIONE PER OGNI ESPERIENZA, NON MODIFICABILE, NON ELIMINABILE


                //DISPLAY OK ICON
                conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                confermata_rifiutata_textview.setText("La tua prenotazione è stata confermata!");
            } else {
                reviews_button.setVisibility(View.GONE);

                //DISPLAY WAITING FOR APPROVAL ICON
            }

        } else { //siamo i creatori
            if (isAccepted) {
                reviews_button.setVisibility(View.GONE);
                conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                //DISPLAY OK ICON
                confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                confermata_rifiutata_textview.setText("La tua prenotazione è stata confermata!");

                final ImageView foto = findViewById(R.id.foto);
                if (photoUri != null) {
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


                foto_utenteIV = findViewById(R.id.profile_pic);
                if (foto_utente_prenotante != null) {
                    storageReference.child(foto_utente_prenotante)
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
                        i.putExtra("ID_PROFILO", ID_PRENOTANTE);
                        i.putExtra("profile_pic", foto_utente_prenotante);
                        i.putExtra("name", nome_utente);

                        startActivity(i);
                    }
                });

            } else {
                reviews_button.setVisibility(View.GONE);
                confermata_rifiutata_textview.setVisibility(View.GONE);
                conferma_rifiuta_prenotazione_layout.setVisibility(View.VISIBLE);
                accetta_esperienza.setEnabled(true);
                rifiuta_esperienza.setEnabled(true);
                //DISPLAY WAITING FOR APPROVAL ICON

                foto = findViewById(R.id.foto);
                if (photoUri != null) {
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


                foto_utenteIV = findViewById(R.id.profile_pic);
                if (foto_utente_prenotante != null) {
                    storageReference.child(foto_utente_prenotante)
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
                        i.putExtra("ID_PROFILO", ID_PRENOTANTE);
                        i.putExtra("profile_pic", foto_utente_prenotante);
                        i.putExtra("name", nome_utente);

                        startActivity(i);
                    }
                });

            }




        }
    }
}
