package com.keiken.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.ColorProto;
import com.keiken.R;
import com.keiken.controller.ImageController;
import com.keiken.model.Esperienza;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ViewExperienceActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_experience);

        // bisogna fare la get del numero di posti effettivo dalla prenotazione

        // calendario -> in base alla data selezionata dal calendario ti dice la disponibilità per quella data


        String titolo = getIntent().getStringExtra("titolo");
        String descrizione = getIntent().getStringExtra("descrizione");
        String luogo = getIntent().getStringExtra("luogo");
        String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        String prezzo = getIntent().getStringExtra("prezzo");
        ArrayList<String> categorie = getIntent().getStringArrayListExtra("categorie");
        String ore = getIntent().getStringExtra("ore");
        String minuti = getIntent().getStringExtra("minuti");
        String postiMax = getIntent().getStringExtra("nPostiDisponibili");
        String photoUri = getIntent().getStringExtra("photoUri");
        HashMap<Calendar, Long> dateMap = (HashMap<Calendar, Long>) getIntent().getSerializableExtra("date");

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



        MaterialButton prenotaEsperienza = findViewById(R.id.prenota_esperienza);
        if(mAuth.getCurrentUser().getUid().equals(ID_CREATORE)){
            prenotaEsperienza.setVisibility(View.GONE);
        }
        prenotaEsperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewExperienceActivity.this, BookExperienceActivity.class));
            }
        });

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


        ArrayList<Calendar> dateList = new ArrayList<Calendar>(dateMap.keySet());

        db = FirebaseFirestore.getInstance();
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

        TextView dateTV = findViewById(R.id.date);
        for(int i = 0; i<dateList.size(); i++){
            String tempDate = dateList.get(i).get(Calendar.DAY_OF_MONTH) + "/";
            if (dateList.get(i).get(Calendar.MONTH) < 10)
                tempDate += "0";
            tempDate += dateList.get(i).get(Calendar.MONTH) + "/" + dateList.get(i).get(Calendar.YEAR);;

            dateTV.setText(dateTV.getText() + tempDate + " (Posti disponibili: "+dateMap.get(dateList.get(i)).toString() + ")\n");
        }


        final TextView user_name = findViewById(R.id.user_name);
        final ImageView profile_pic = findViewById(R.id.profile_pic);
        //NOME E FOTO CREATORE
        CollectionReference utenti = db.collection("utenti");
        Query query = utenti.whereEqualTo("id", ID_CREATORE);
        Task<QuerySnapshot> querySnapshotTask = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    try {
                        QuerySnapshot result = task.getResult();
                        List<DocumentSnapshot> documents = result.getDocuments();
                        DocumentSnapshot document = documents.get(0);


                        user_name.setText((String) document.get("name"));
                        String photoUrl = (String) document.get("photoUrl");


                        if (photoUrl != null)
                            new ImageController.DownloadImageFromInternet(profile_pic).execute(photoUrl);
                    } catch (Exception e ) {};

                }

            }
        });





    }
}
