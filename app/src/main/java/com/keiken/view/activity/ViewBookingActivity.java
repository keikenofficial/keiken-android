package com.keiken.view.activity;

import android.content.Intent;
import android.media.Image;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;

import org.w3c.dom.Document;

import java.sql.Struct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewBookingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);





        //GET INTENT
        final String titolo = getIntent().getStringExtra("titolo");
        final String descrizione = getIntent().getStringExtra("descrizione");
        final String luogo = getIntent().getStringExtra("luogo");
        //final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        //final String ID_PRENOTANTE = getIntent().getStringExtra("ID_PRENOTANTE");
        //final String prezzo = getIntent().getStringExtra("prezzo");
        //String ore = getIntent().getStringExtra("ore");
        //String minuti = getIntent().getStringExtra("minuti");
        //final String posti_prenotati = getIntent().getStringExtra("posti_prenotati");

        final String photoUri = getIntent().getStringExtra("photoUri");
        //String data_prenotazione = getIntent().getStringExtra("data_prenotazione");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");
        /*final String nome_utente = getIntent().getStringExtra("nome_utente");
        final String foto_utente = getIntent().getStringExtra("photo_url_creatore_esperienza");
        final String foto_utente_prenotante = getIntent().getStringExtra("photo_url_prenotante_esperienza");
        boolean isAccepted = getIntent().getExtras().getBoolean("isAccepted");*/
        final String ID_PRENOTAZIONE = getIntent().getStringExtra("ID_PRENOTAZIONE");

        final LinearLayout profilo = findViewById(R.id.account);

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

        ////////////////


        final TextView confermata_rifiutata_textview = findViewById(R.id.confermata_rifiutata_textview);
        confermata_rifiutata_textview.setVisibility(View.GONE);

        final LinearLayout conferma_rifiuta_prenotazione_layout =findViewById(R.id.conferma_rifiuta_prenotazione_layout);
        conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);

        final MaterialButton accetta_esperienza = findViewById(R.id.accetta_esperienza);
        final MaterialButton rifiuta_esperienza = findViewById(R.id.rifiuta_esperienza);

        final TextView prezzoTV = findViewById(R.id.prezzo);

        final TextView orarioTV = findViewById(R.id.orario);
        final TextView posti_prenotatiTV = findViewById(R.id.posti_prenotati);
        final TextView luogoTV = findViewById(R.id.luogo);
        final TextView dateTV = findViewById(R.id.date);
        final ImageView foto = findViewById(R.id.foto);
        final TextView user_name = findViewById(R.id.nome_utente);
        final ImageView foto_utenteIV = findViewById(R.id.profile_pic);
        final TextView descrizioneTV = findViewById(R.id.descrizione);


        accetta_esperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                                    conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                    //DISPLAY OK ICON
                                    confermata_rifiutata_textview.setBackgroundColor(65280);
                                    confermata_rifiutata_textview.setText("Hai confermato la prenotazione");
                                    confermata_rifiutata_textview.setVisibility(View.VISIBLE);


                                    Map<String, Object> map = new HashMap<>();
                                    map.put("isAccepted", true);
                                    db.collection("prenotazioni").document(ID_PRENOTAZIONE).update(map);
                                    Log.d("", "Modifica prenotazione accettata. ");


                                }
                            }
                        }
                    });
                }catch (NullPointerException e){
                    accetta_esperienza.setEnabled(true);
                    rifiuta_esperienza.setEnabled(true);
                }
            }
        });

        rifiuta_esperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accetta_esperienza.setEnabled(false);
                rifiuta_esperienza.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Hai rifiutato la prenotazione.", Toast.LENGTH_LONG).show();
                onBackPressed();


                //prendo i posti prenotati
                final DocumentReference prenotazioniDb = db.collection("prenotazioni").document(ID_PRENOTAZIONE);
                prenotazioniDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            final long posti_prenotati_da_eliminare = (Long) document.get("posti_prenotati");
                            final Timestamp timestamp_prenotazione = (Timestamp) document.get("data_selezionata");

                            db.collection("prenotazioni").document(ID_PRENOTAZIONE)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("", "DocumentSnapshot successfully deleted!");
                                            try{
                                                //UPDATE POSTI DISPONIBILI INTERROGO IL DATABASE VIA ID_ESPERIENZA
                                                final CollectionReference esperienzeDb = db.collection("esperienze").document(ID_ESPERIENZA).collection("date");
                                                esperienzeDb.whereEqualTo("data", timestamp_prenotazione).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task){
                                                        if(task.isSuccessful()){

                                                            for(DocumentSnapshot document_to_update : task.getResult()){
                                                                if(document_to_update.exists()){
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    long posti_disponibili = (Long) document_to_update.get("posti_disponibili");
                                                                    long new_posti = posti_disponibili + posti_prenotati_da_eliminare;
                                                                    map.put("posti_disponibili", new_posti);
                                                                    esperienzeDb.document(document_to_update.getId()).update(map);
                                                                }
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
                    }
                });
            }
        });

        ///////////////////////////////////////////////


        final DocumentReference prenotazioniDb = db.collection("prenotazioni").document(ID_PRENOTAZIONE);
        prenotazioniDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        String ID_CREATORE_ESPERIENZA = (String) document.get("ID_CREATORE_ESPERIENZA");
                        String ID_PRENOTANTE = (String) document.get("ID_PRENOTANTE");
                        String prezzo = (String) document.get("prezzo");
                        String ore = (String) document.get("ore");
                        String minuti = (String) document.get("minuti");
                        long posti_prenotati = (Long) document.get("posti_prenotati");
                        Timestamp timestamp = (Timestamp) document.get("data_selezionata");
                        Date data_prenotazione = timestamp.toDate();
                        boolean isAccepted = (boolean) document.get("isAccepted");

                        float prezzo_totale = Float.parseFloat(prezzo) * posti_prenotati;

                        prezzoTV.setText("Prezzo totale: " + prezzo_totale + "\u20AC");

                        int h = Integer.parseInt(ore);
                        int min = Integer.parseInt(minuti);
                        if (h <10)
                            ore ="0" + ore;
                        if (min <10)
                            minuti ="0" + min;
                        orarioTV.setText(ore+":"+minuti);
                        posti_prenotatiTV.setText("Posti prenotati: " + posti_prenotati);
                        descrizioneTV.setText(descrizione);
                        luogoTV.setText(luogo);
                        final SimpleDateFormat formatYear = new SimpleDateFormat("YYYY");
                        String currentYear = formatYear.format(data_prenotazione.getTime());
                        dateTV.setText(data_prenotazione.toString().substring(0,10)+" " +currentYear);
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

                        if(ID_CREATORE_ESPERIENZA.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            //SONO IL CREATORE
                            final DocumentReference utentiDb = db.collection("utenti").document(ID_PRENOTANTE);
                            utentiDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();

                                        final String nome_utente_prenotante = (String) document.get("name");
                                        final String foto_utente_prenotante = (String) document.get("photoUrl");
                                        final String cognome_utente_prenotante = (String) document.get("surname");
                                        final String bio = (String) document.get("bio");
                                        final String email = (String ) document.get("email");
                                        final String day = (String ) document.get("day");
                                        final String month = (String ) document.get("month");
                                        final String year = (String ) document.get("year");
                                        final boolean publicSurname = (boolean) document.get("publicSurname");
                                        final boolean publicEmail = (boolean) document.get("publicEmail");
                                        final boolean publicDate = (boolean) document.get("publicDate");



                                        user_name.setText(nome_utente_prenotante);


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
                                                i.putExtra("ID_PROFILO", document.getId());
                                                i.putExtra("profile_pic", foto_utente_prenotante);
                                                i.putExtra("name", nome_utente_prenotante);
                                                i.putExtra("surname", cognome_utente_prenotante);
                                                i.putExtra("bio", bio);
                                                i.putExtra("email", email);
                                                i.putExtra("day", day);
                                                i.putExtra("month", month);
                                                i.putExtra("year", year);
                                                i.putExtra("publicSurname", publicSurname);
                                                i.putExtra("publicEmail", publicEmail);
                                                i.putExtra("publicDate", publicDate);




                                                startActivity(i);
                                            }
                                        });
                                    }
                                }
                            });
                            if (isAccepted) {
                                conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                //DISPLAY OK ICON
                                confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                                confermata_rifiutata_textview.setText("Hai confermato la prenotazione. ");
                            } else {
                                conferma_rifiuta_prenotazione_layout.setVisibility((View.VISIBLE));
                                confermata_rifiutata_textview.setVisibility(View.GONE);
                                //DISPLAY BUTTONS

                                accetta_esperienza.setEnabled(true);
                                rifiuta_esperienza.setEnabled(true);
                            }

                        } else {
                            //SONO IL PRENOTANTE
                            //carico foto e nome utente
                            final DocumentReference utentiDb = db.collection("utenti").document(ID_CREATORE_ESPERIENZA);
                            utentiDb.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        final DocumentSnapshot document = task.getResult();
                                        final String nome_utente = (String) document.get("name");
                                        final String foto_utente = (String) document.get("photoUrl");
                                        final String cognome_utente = (String) document.get("surname");

                                        user_name.setText(nome_utente);


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
                                                i.putExtra("ID_PROFILO", document.getId());
                                                i.putExtra("profile_pic", foto_utente);
                                                i.putExtra("name", nome_utente);
                                                i.putExtra("surname", cognome_utente);

                                                startActivity(i);
                                            }
                                        });
                                    }
                                }
                            });

                            if (isAccepted) {

                                //ON CLICK HANDLER PER CREARE RECENSIONI.
                                //è POSSIBILE SCRIVERE UNA VOOLTA SOLA LA RECENSIONE PER OGNI ESPERIENZA, NON MODIFICABILE, NON ELIMINABILE


                                //DISPLAY OK ICON
                                conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                                confermata_rifiutata_textview.setText("La tua prenotazione è stata confermata!");
                            } else {
                                //DISPLAY IN PROGRSS
                                conferma_rifiuta_prenotazione_layout.setVisibility(View.GONE);
                                confermata_rifiutata_textview.setVisibility(View.VISIBLE);
                                confermata_rifiutata_textview.setBackgroundColor(65280); //GREEN
                                confermata_rifiutata_textview.setText("Prenotazione in attesa di conferma");

                            }
                        }
                }
            }
        });
    }
}
