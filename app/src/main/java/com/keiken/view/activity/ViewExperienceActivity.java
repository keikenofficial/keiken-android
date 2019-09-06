package com.keiken.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.type.ColorProto;
import com.keiken.R;
import com.keiken.model.Esperienza;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ViewExperienceActivity extends AppCompatActivity {

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

        TextView descrizioneTV = findViewById(R.id.descrizione);
        descrizioneTV.setText(descrizione);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo a persona: " + prezzo + "\u20AC");

        TextView orarioTV = findViewById(R.id.orario);
        orarioTV.setText(ore+":"+minuti);

        TextView postiMaxTV = findViewById(R.id.posti_disponibili);
        postiMaxTV.setText("Disponibilità massima: " + postiMax);

        TextView dateTV = findViewById(R.id.date);

        ArrayList<Calendar> dateList = (ArrayList<Calendar>) dateMap.keySet();
        for(int i = 0; i< dateList.size(); i++) {
            dateTV.setText(dateTV.getText() + "\n" +
                    dateMap.get(dateList.get(i)).toString() + "\n"  // numeri posti
                    + dateList.get(i).toString()); // roba
        }
    }
}
