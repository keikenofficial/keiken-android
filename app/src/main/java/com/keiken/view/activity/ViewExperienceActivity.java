package com.keiken.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.ComparatorCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ViewExperienceActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String minuti;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_experience);

        // bisogna fare la get del numero di posti effettivo dalla prenotazione

        // calendario -> in base alla data selezionata dal calendario ti dice la disponibilità per quella data


        final String titolo = getIntent().getStringExtra("titolo");
        String descrizione = getIntent().getStringExtra("descrizione");
        final String luogo = getIntent().getStringExtra("luogo");
        final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        final String prezzo = getIntent().getStringExtra("prezzo");
        ArrayList<String> categorie = getIntent().getStringArrayListExtra("categorie");
        String ore = getIntent().getStringExtra("ore");
        minuti = getIntent().getStringExtra("minuti");
        String postiMax = getIntent().getStringExtra("nPostiDisponibili");
        String photoUri = getIntent().getStringExtra("photoUri");
        final HashMap<Date, Long> dateMap = (HashMap<Date, Long>) getIntent().getSerializableExtra("date");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");
        final LinearLayout profilo = findViewById(R.id.account);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(titolo);

        collapsingToolbarLayout.setStatusBarScrimColor(Color.TRANSPARENT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth=FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



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



        TextView descrizioneTV = findViewById(R.id.descrizione);
        descrizioneTV.setText(descrizione);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo a persona: " + prezzo + "\u20AC");

        TextView orarioTV = findViewById(R.id.orario);
        int h = Integer.parseInt(ore);
        int min = Integer.parseInt(minuti);
        if (h <10)
            ore ="0" + ore;
        if (min <10)
            minuti ="0" + min;
        orarioTV.setText(ore+":"+minuti);


        Chip culturaC = findViewById(R.id.cultura);
        Chip sportC = findViewById(R.id.sport);
        Chip musicaC = findViewById(R.id.musica);
        Chip ciboC = findViewById(R.id.cibo);
        Chip divertimentoC = findViewById(R.id.divertimento);
        for(String categoria : categorie){
            switch (categoria){
                case "Cultura":
                    culturaC.setVisibility(View.VISIBLE);
                    break;
                case "Sport":
                    sportC.setVisibility(View.VISIBLE);
                    break;
                case "Musica":
                    musicaC.setVisibility(View.VISIBLE);
                    break;
                case "Cibo":
                    ciboC.setVisibility(View.VISIBLE);
                    break;
                case "Divertimento":
                    divertimentoC.setVisibility(View.VISIBLE);
                    break;
            }


        }

        ArrayList<Date> dateList = new ArrayList<Date>(dateMap.keySet());
        Collections.sort(dateList);


        TextView dateTV = findViewById(R.id.date);
        for(int i = 0; i<dateList.size(); i++){
        /*    String tempDate = "";
            if (dateList.get(i).get(Calendar.DAY_OF_MONTH) < 10)
                tempDate += "0";
             tempDate += dateList.get(i).get(Calendar.DAY_OF_MONTH) + "/";
            if (dateList.get(i).get(Calendar.MONTH) < 10)
                tempDate += "0";
            tempDate += dateList.get(i).get(Calendar.MONTH) + "/" + dateList.get(i).get(Calendar.YEAR);;

         */

            SimpleDateFormat formatYear = new SimpleDateFormat("YYYY");
            String currentYear = formatYear.format(dateList.get(i));
            dateTV.setText(dateTV.getText() + dateList.get(i).toString().substring(0,10) + " " +currentYear + " (Posti disponibili: "+dateMap.get(dateList.get(i)).toString() + ")\n");
        }


        //NOME E FOTO CREATORE
        CollectionReference utenti = db.collection("utenti");
        Query query = utenti.whereEqualTo("id", ID_CREATORE);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    List<DocumentSnapshot> documents = result.getDocuments();
                    DocumentSnapshot document = documents.get(0);

                    final ImageView profile_pic = findViewById(R.id.profile_pic);
                    final String photoUrl = (String) document.get("photoUrl");
                    final String nome_utente = (String) document.get("name");
                    final String cognome_utente = (String) document.get("surname");
                    final String bio = (String) document.get("bio");
                    final String email = (String) document.get("email");
                    final String day = (String) document.get("day");
                    final String month = (String) document.get("month");
                    final String year = (String) document.get("year");
                    final boolean publicSurname = (boolean) document.get("publicSurname");
                    final boolean publicEmail = (boolean) document.get("publicEmail");
                    final boolean publicDate = (boolean) document.get("publicDate");


                    profilo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (ID_CREATORE.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                onBackPressed();
                            else {
                                Intent i = new Intent(ViewExperienceActivity.this, ViewProfileActivity.class);
                                //passo i parametri per la visualizzazione del profilo
                                i.putExtra("ID_PROFILO", ID_CREATORE);
                                i.putExtra("profile_pic", photoUrl);
                                i.putExtra("name", nome_utente);
                                i.putExtra("surname", cognome_utente);
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
                        }
                    });

                    TextView user_name = findViewById(R.id.nome_utente);

                    user_name.setText((String) document.get("name"));




                    if(photoUrl != null) {
                        storageReference.child(photoUrl)
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'photos/profile.png'

                                new ImageController.DownloadImageFromInternet(profile_pic).execute(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any error
                            }
                        });
                    }
                }
            }
        });


        MaterialButton prenotaEsperienza = findViewById(R.id.prenota_esperienza);
        if(mAuth.getCurrentUser().getUid().equals(ID_CREATORE)){
            prenotaEsperienza.setVisibility(View.GONE);
        }


        final String finalOre = ore;
        prenotaEsperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewExperienceActivity.this, BookExperienceActivity.class);

                //passo i parametri per la prenotazione in modo da non dover interrogare il database nuovamente
                i.putExtra("titolo", titolo);
                i.putExtra("luogo", luogo);
                i.putExtra("ore", finalOre);
                i.putExtra("minuti", minuti);
                i.putExtra("date", dateMap); // HAS MAP CON <CALENDAR, LONG> ----> <DATA, N_POSTI_DISPONIBILI>
                //ALL'ATTO DELLA PRENOTAZIONE VA INTERROGATO IL DATABASE PER
                //CONTROLLARE IL NUMERO DI POSTI DISPONIBILI
                i.putExtra("prezzo", prezzo);
                i.putExtra("ID_CREATORE", ID_CREATORE);
                i.putExtra("ID_ESPERIENZA", ID_ESPERIENZA);


                startActivity(i);
            }
        });
    }
}
