package com.keiken.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Document;

import static com.keiken.controller.ImageController.createVoidImageFile;

public class BookExperienceActivity extends AppCompatActivity {

    private BackdropFrontLayerBehavior sheetBehavior;
    private int peekHeight = 0;


    static final int REQUEST_PHOTO = 1889;
    private static final String NON_NORMAL_CHARACTERS_PATTERN = "\\W|[^!@#\\$%\\^&\\*\\(\\)]";
    private StorageReference storageReference;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_experience);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();





        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);

        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


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

                    int toolbarPx = (int) (80 * (displayMetrics.densityDpi / 160f));

                    peekHeight = displayMetrics.heightPixels - viewHeight - toolbarPx;

                    sheetBehavior.setPeekHeight(peekHeight);

                }
            });
        }


        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Prenota esperienza");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ///////////////////////////////


        final String titolo = getIntent().getStringExtra("titolo");
        final String luogo = getIntent().getStringExtra("luogo");
        final String ID_CREATORE = getIntent().getStringExtra("ID_CREATORE");
        final String prezzo = getIntent().getStringExtra("prezzo");
        final String ore = getIntent().getStringExtra("ore");
        final String minuti = getIntent().getStringExtra("minuti");
        final HashMap<Date, Long> dateMap = (HashMap<Date, Long>) getIntent().getSerializableExtra("date");
        final String ID_ESPERIENZA = getIntent().getStringExtra("ID_ESPERIENZA");


        TextView titoloTV = findViewById(R.id.titolo);
        titoloTV.setText(titolo);

        TextView luogoTV = findViewById(R.id.luogo);
        luogoTV.setText(luogo);

        TextView prezzoTV = findViewById(R.id.prezzo);
        prezzoTV.setText("Prezzo a persona: " + prezzo + "\u20AC");

        TextView orarioTV = findViewById(R.id.orario);
        orarioTV.setText(ore+":"+minuti);

        //Visualizza elenco date selezionabili
        final ArrayList<Date> dateList = new ArrayList<Date>(dateMap.keySet());
        ArrayList<String> dateListString = new ArrayList<String>();

        for(Date d: dateList){
           // String tempDate = dateList.get(i).get(Calendar.DAY_OF_MONTH) + "/";
           // if (dateList.get(i).get(Calendar.MONTH) < 10)
           //     tempDate += "0";
           // tempDate += dateList.get(i).get(Calendar.MONTH) + "/" + dateList.get(i).get(Calendar.YEAR);

            dateListString.add(d.toString().substring(0,10)+" 2019");

        }
        //final ArrayList<String> dateListStringToDB = new ArrayList<String>(dateListString);

        final String[] dateToArray = new String[dateListString.size()];

        for(int i = 0; i<dateListString.size(); i++){
            dateToArray[i] = dateListString.get(i);
        }

        final NumberPicker date_selection = findViewById(R.id.date_selection);
        date_selection.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        date_selection.setMinValue(0);
        date_selection.setMaxValue(dateMap.size()-1);
        date_selection.setDisplayedValues(dateToArray);

        final NumberPicker posti_disponibili_picker = findViewById(R.id.posti_disponibili);
        posti_disponibili_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //DA TESTARE, SETTA PER LA PRIMA DATA IL NUMERO DI POSTI
        posti_disponibili_picker.setMinValue(1);
        int maxValue = Integer.valueOf(dateMap.get((dateList.get(date_selection.getValue()))).intValue());
        posti_disponibili_picker.setMaxValue(maxValue);

        final MaterialButton prenotaBT = findViewById(R.id.prenota_esperienza);
        if(!(Integer.valueOf(dateMap.get((dateList.get(date_selection.getValue()))).intValue()) > 0)){
            prenotaBT.setEnabled(false);
        }
        //Set a value change listener for NumberPicker
        date_selection.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){

                int maxValue = Integer.valueOf(dateMap.get((dateList.get(date_selection.getValue()))).intValue()); //POCO ELEGANTE, SAREBBE MEGLIO TROVARE UNA SOLUZIONE ALTERNATIVA
                if(maxValue>0) {
                    posti_disponibili_picker.setVisibility(View.VISIBLE);
                    posti_disponibili_picker.setMinValue(1);
                    posti_disponibili_picker.setMaxValue(maxValue);
                    prenotaBT.setEnabled(true);
                } else {
                    posti_disponibili_picker.setVisibility(View.GONE);
                    prenotaBT.setEnabled(false);
                }
            }
        });





        prenotaBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //START ON CLICK

                prenotaBT.setEnabled(false);

                //SISTEMA PAGAMENTI --- DA IMPLEMENTARE/////
                //
                // QUI AVVIENE IL PAGAMENTO, SE HA SUCCESSO SI PASSA AL SALVATAGGIO DELLA PRENOTAZIONE SUL DATABASE
                //
                ///////////////////////////////////////////


                //SALVATAGGIO PRENOTAZIONE SUL DATABASE
                /////// DA CONTROLLARE //////////////////
                // Create a new booking
                final Map<String, Object> bookingDb = new HashMap<>();
                bookingDb.put("ID_CREATORE_ESPERIENZA", ID_CREATORE);
                bookingDb.put("ID_PRENOTANTE", mAuth.getCurrentUser().getUid());
                bookingDb.put("ID_ESPERIENZA", ID_ESPERIENZA);
                bookingDb.put("posti_prenotati", posti_disponibili_picker.getValue());
                bookingDb.put("data_selezionata", dateList.get(date_selection.getValue()));
                bookingDb.put("ore", ore);
                bookingDb.put("minuti", minuti);
                bookingDb.put("prezzo", prezzo);
                bookingDb.put("isAccepted", false);

                //Aggiorno i dati della prenotazione salvata sul dabase in base ai dati registrati
                final CollectionReference data = db.collection("esperienze").document(ID_ESPERIENZA).collection("date");
                data.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()){
                                if(document.exists()){
                                    Long nPostiDisponibili = (Long) document.get("posti_disponibili");
                                    Long posti_prenotati = new Long(posti_disponibili_picker.getValue());
                                    Long posti_rimanenti = nPostiDisponibili - posti_prenotati;


                                   // Timestamp date = (Timestamp) document.get("data");
                                   // Long tempTimestamp = date.toDate().getTime();
                                   // Long tempTimestamp = (Long) ((HashMap<String, Object>) document.get("data")).get("timeInMillis");
                                   // Calendar tempCalendar = new GregorianCalendar();
                                    //tempCalendar.setTimeInMillis(tempTimestamp);

            //TODO 1 -   DA CONTROLLARE    QUESTA RIGA E SE SERVE DE-COMMENTARLA E MODIFICARLA OPPORTUNAMENTE
                                    // TODO 1 - if( (tempCalendar.get(Calendar.DAY_OF_MONTH) == dateList.get(date_selection.getValue()).get(Calendar.DAY_OF_MONTH) ) && ( tempCalendar.get(Calendar.MONTH) == dateList.get(date_selection.getValue()).get(Calendar.MONTH) ) && ( tempCalendar.get(Calendar.YEAR) == dateList.get(date_selection.getValue()).get(Calendar.YEAR)) ) {

                      //TODO 2 - DA CONTROLLARE, MODIFICARE E RIFARE                  if (posti_rimanenti >= posti_prenotati) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("posti_disponibili", posti_rimanenti);
                                            data.document(document.getId()).update(map);

                                            // Add a new document with a generated ID
                                            try {
                                                db.collection("prenotazioni")
                                                        .add(bookingDb)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d("", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                startActivity(new Intent(BookExperienceActivity.this, HomeActivity.class));

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("", "Error adding document", e);
                                                                prenotaBT.setEnabled(true);
                                                            }
                                                        });
                                            } catch(NullPointerException e){
                                                Toast.makeText(getApplicationContext(), "Errore nel raggiungere il server, porva a fare di nuovo il login.", Toast.LENGTH_LONG).show();
                                                prenotaBT.setEnabled(true);
                                    //TODO  - 2      }
                                    //TODO - 2    }else{
                                      //TODO -2       Toast.makeText(getApplicationContext(), "Ci dispiace, per la data che hai scelto non ci sono più posti disponibili.", Toast.LENGTH_LONG).show();
                                      //TODO -2      prenotaBT.setEnabled(true);
                                        }
                                   // } TODO - 1
                                }
                            }
                        }
                    }
                });//Fine update
                ////////////////////////////////////////////////////////////////////////////
                //END ON CLICK
            }
        });



    }





    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

}

