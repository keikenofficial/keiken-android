package com.keiken.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.keiken.R;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.keiken.controller.ImageController.*;
import static java.security.AccessController.getContext;

public class CreateExperienceActivity extends AppCompatActivity {

    private BackdropFrontLayerBehavior sheetBehavior;
    private int peekHeight = 0;


    static final int REQUEST_PHOTO = 1889;
    private static final String NON_NORMAL_CHARACTERS_PATTERN = "\\W|[^!@#\\$%\\^&\\*\\(\\)]";
    private StorageReference storageReference;
    private Uri storageUrl = null;
    private ImageView imageView;
    private ProgressDialog progressDialog = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private Uri uri_definitivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_experience);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        imageView = findViewById(R.id.photo);
        Button confirmCreaEsperienza = findViewById(R.id.confirm_crea_esperienza);


        final EditText titoloEditText = findViewById(R.id.titolo_edit);
        final EditText descrizioneEditText = findViewById(R.id.descrizione_edit);
        final EditText luogoEditText = findViewById(R.id.luogo);
        final EditText prezzoEditText = findViewById(R.id.prezzo);
        final ChipGroup categoriaChip = findViewById(R.id.categorie);
        final TimePicker timePicker = findViewById(R.id.timePicker);
        final NumberPicker pickerPosti = findViewById(R.id.posti_disponibili);


        final BackdropFrontLayer contentLayout = findViewById(R.id.backdrop);
        Button changePhotoButton = findViewById(R.id.change_photo);

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

                    int toolbarPx = (int)( 80 * (displayMetrics.densityDpi / 160f));

                    peekHeight = displayMetrics.heightPixels-viewHeight-toolbarPx;

                    sheetBehavior.setPeekHeight(peekHeight);

                }
            });
        }





        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Crea esperienza");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ///////////////////////////////


        //posti disponibili////////////////

        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        pickerPosti.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        pickerPosti.setMaxValue(99);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        pickerPosti.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        pickerPosti.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){

            }
        });

        ///////////////////////////




        //CALENDARIO

        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.thumb_primary_color));
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
            }
        });

        List<Calendar> selectedDates = calendarView.getSelectedDates();
        /////////////////////////////

        changePhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                        || ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

                        || ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            0);
                }
                else {
                    if(titoloEditText.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Imposta un titolo prima di modificare l'immagine o controlla che non contenga caratteri speciali.", Toast.LENGTH_LONG).show();
                    } else selectImage();
                }
            }
        });

        confirmCreaEsperienza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //confirm profile changes on database



                /////////////
                final String titolo = titoloEditText.getText().toString();
                final String descrizione = descrizioneEditText.getText().toString();
                final String luogo = luogoEditText.getText().toString();
                final String prezzo = prezzoEditText.getText().toString();

                //categorie
                final ChipGroup categoriaChip = findViewById(R.id.categorie);
                ArrayList<String> categorie = new ArrayList<String>();
                for(int i=0; i< categoriaChip.getChildCount(); i++){
                    Chip child = (Chip) categoriaChip.getChildAt(i);

                    if(child.isSelected()){
                        categorie.add(child.getText().toString());
                    }
                }
                //////////////

                //ORARIO
                final TimePicker timePicker = findViewById(R.id.timePicker);
                int ore = timePicker.getHour(); //restituisce le ore in formato 24H
                int minuti = timePicker.getMinute();
                //////////////


                final NumberPicker pickerPosti = findViewById(R.id.posti_disponibili);
                int nPostiDisponibili = pickerPosti.getValue();


                //upload dell'immagine da eseguire previa verifica di tutti i dati
                uploadEsperienzaImage(uri_definitivo);

            }
        });
    }


    private boolean verifyInformations(String titolo, String descrizione, String luogo){
        if(titolo.equals("")){
            Toast.makeText(getApplicationContext(), "Il titolo non può essere vuoto o contenere caratteri speciali.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(descrizione.equals("")){
            Toast.makeText(getApplicationContext(), "La descrizione non può essere vuota o contenere caratteri speciali.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(luogo.equals("")){
            Toast.makeText(getApplicationContext(), "Il luogo non può essere vuoto o contenere caratteri speciali.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            if (data == null) { //camera
                try {
                    //uploadEsperienzaImage(storageUrl);
                    uri_definitivo=storageUrl;

                    imageView.setImageURI(storageUrl);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(storageUrl);
                    Objects.requireNonNull(this).sendBroadcast(mediaScanIntent);
                } catch (Exception e) {
                    Log.e("Error uploading file", e.getMessage());
                }
            } else { //gallery
                Uri filePath = data.getData();
                imageView.setImageURI(filePath);
                //uploadEsperienzaImage(filePath);
                uri_definitivo=filePath;
            }
        }
    }

    private void uploadEsperienzaImage(Uri filePath) {

        if (filePath != null) {
            final EditText titoloEditText = findViewById(R.id.titolo_edit);
            String titolo =  titoloEditText.getText().toString();

            final StorageReference ref = storageReference.child("images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().concat("/")+"esperienze/"+titolo);
            ref.putFile(filePath);
        }

    }

    @Override
    public void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    private void selectImage() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "Seleziona app:");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        storageUrl = Uri.fromFile(createVoidImageFile());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, storageUrl);

        startActivityForResult(chooser, REQUEST_PHOTO);
    } //start an activity calling camera and gallety intent to edit profile photo


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            selectImage();
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
            super.onBackPressed();
    }




}

