package com.keiken.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
        final HashMap<Calendar, Long> dateMap = (HashMap<Calendar, Long>) getIntent().getSerializableExtra("date");
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
        final ArrayList<Calendar> dateList = new ArrayList<Calendar>(dateMap.keySet());
        ArrayList<String> dateListString = new ArrayList<String>();

        for(int i = 0; i<dateList.size(); i++){
            String tempDate = dateList.get(i).get(Calendar.DAY_OF_MONTH) + "/";
            if (dateList.get(i).get(Calendar.MONTH) < 10)
                tempDate += "0";
            tempDate += dateList.get(i).get(Calendar.MONTH) + "/" + dateList.get(i).get(Calendar.YEAR);

            dateListString.add(tempDate);

        }


        final String[] dateToArray = new String[dateListString.size()];

        for(int i = 0; i<dateListString.size(); i++){
            dateToArray[i] = dateListString.get(i);
        }

        final NumberPicker date_selection = findViewById(R.id.date_selection);
        date_selection.setMinValue(0);
        date_selection.setMaxValue(dateMap.size()-1);
        date_selection.setDisplayedValues(dateToArray);

        final NumberPicker posti_disponibili_picker = findViewById(R.id.posti_disponibili);

        //DA TESTARE, SETTA PER LA PRIMA DATA IL NUMERO DI POSTI
        posti_disponibili_picker.setMinValue(1);
        int maxValue = Integer.valueOf(dateMap.get((dateList.get(date_selection.getValue()))).intValue());
        posti_disponibili_picker.setMaxValue(maxValue);

        //Set a value change listener for NumberPicker
        date_selection.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                posti_disponibili_picker.setMinValue(1);
                int maxValue = Integer.valueOf(dateMap.get((dateList.get(date_selection.getValue()))).intValue());
                posti_disponibili_picker.setMaxValue(maxValue);
            }
        });


        MaterialButton prenotaBT = findViewById(R.id.prenota_esperienza);


        prenotaBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //START ON CLICK






                //END ON CLICK
            }
        });
    }





    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

}

