package com.keiken.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.services.common.Crash;

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

    private boolean isPhotoSelected = false;

    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;
    public static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;


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
        final Button confirmCreaEsperienza = findViewById(R.id.confirm_crea_esperienza);


        final EditText titoloEditText = findViewById(R.id.titolo_edit);
        final EditText descrizioneEditText = findViewById(R.id.descrizione_edit);
        final EditText luogoEditText = findViewById(R.id.luogo);
        final EditText prezzoEditText = findViewById(R.id.prezzo);
        prezzoEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});




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
        final CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);
        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
            }
        });
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
                    } else
                        selectImage();
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

                    if(child.isChecked()){
                        categorie.add(child.getText().toString());
                    }
                }
                //////////////

                //CALENDARIO
                final List<Calendar> selectedDates = calendarView.getSelectedDates();
                ///////////
                final int nPostiDisponibili = pickerPosti.getValue();

                //CREA COLLECTION DA INSERIRE NEL DATABASE


                //ORARIO
                final TimePicker timePicker = findViewById(R.id.timePicker);
                int ore = timePicker.getHour(); //restituisce le ore in formato 24H
                int minuti = timePicker.getMinute();
                //////////////

                String ID_CREATORE = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final NumberPicker pickerPosti = findViewById(R.id.posti_disponibili);

                String uri = "images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().concat("/")+"esperienze/"+titolo;



                if(verifyInformations(titolo, descrizione, luogo) && isPrezzoValid(prezzo) && isCategorieValid(categorie) && isDateValid(selectedDates, ore, minuti) && isPhotoValid()) {
                    confirmCreaEsperienza.setEnabled(false);
                    //upload dell'immagine da eseguire previa verifica di tutti i dati
                    uploadEsperienzaImage(uri_definitivo);

                    CollectionReference esperienze = db.collection("esperienze");
                    // Create a new experience
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> esperienzeDb = new HashMap<>();

                    esperienzeDb.put("ID_CREATORE", ID_CREATORE);

                    esperienzeDb.put("titolo", titolo);
                    esperienzeDb.put("descrizione", descrizione);
                    esperienzeDb.put("luogo", luogo);
                    esperienzeDb.put("prezzo", prezzo);
                    //categorie
                    esperienzeDb.put("categorie", categorie);

                    //orario
                    esperienzeDb.put("ore", ore);
                    esperienzeDb.put("minuti", minuti);
                    esperienzeDb.put("posti_massimi", nPostiDisponibili);

                    esperienzeDb.put("photoUri", uri);
                    try{
                        db.collection("esperienze")
                                .add(esperienzeDb)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("", "DocumentSnapshot added with ID: " + documentReference.getId());

                                        String ID_ESPERIENZA = documentReference.getId();

                                        String updatedPhotoUri = "images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().concat("/")+"esperienze/"+ID_ESPERIENZA;

                                        //change photoUri to match  ID_EXPERIENCE
                                        try {
                                            db.collection("esperienze").document(ID_ESPERIENZA).update("photoUri", updatedPhotoUri).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.w("","updated photoUri");
                                                }
                                            });
                                        } catch (NullPointerException e) {
                                            Toast.makeText(getApplicationContext(), "Errore nel raggiungere il server, porva a fare di nuovo il login.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(CreateExperienceActivity.this, HomeActivity.class));
                                        }

                                        //INIZIALIZZAZIONE DATI

                                        CollectionReference dates = db.collection("esperienze").document(ID_ESPERIENZA).collection("date");
                                        Map<String, Object> dateDb = new HashMap<>();

                                        for(Calendar tempCalendar : selectedDates) {
                                            dateDb.put("data", tempCalendar.getTime());
                                            dateDb.put("posti_disponibili", nPostiDisponibili);

                                            try {
                                                db.collection("esperienze").document(ID_ESPERIENZA).collection("date").add(dateDb)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference> () {
                                                            @Override
                                                                    public void onSuccess(DocumentReference documentReference){

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("", "Error adding document", e);
                                                        confirmCreaEsperienza.setEnabled(true);
                                                        startActivity(new Intent(CreateExperienceActivity.this, HomeActivity.class));
                                                    }
                                                });



                                            } catch (NullPointerException e) {
                                                Toast.makeText(getApplicationContext(), "Errore nel raggiungere il server, prova a fare di nuovo il login.", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(CreateExperienceActivity.this, HomeActivity.class));
                                            }


                                        }

                                        startActivity(new Intent(CreateExperienceActivity.this, HomeActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("", "Error adding document", e);
                                        confirmCreaEsperienza.setEnabled(true);
                                    }
                                });
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "Errore nel raggiungere il server, porva a fare di nuovo il login.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CreateExperienceActivity.this, HomeActivity.class));
                    }



                }
            }
        });
    }

    private boolean isPrezzoValid(String prezzo){
        if (prezzo.equals("")){
                Toast.makeText(getApplicationContext(), "Inserisci il prezzo.", Toast.LENGTH_LONG).show();
                return false;
        }
        return true;
    }

    private boolean isPhotoValid(){
        if(isPhotoSelected){
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Inserisci una foto.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isCategorieValid(ArrayList<String> categorie){
        if(categorie.size() == 0){
            Toast.makeText(getApplicationContext(), "Seleziona almeno una categoria.", Toast.LENGTH_LONG).show();
            return false;
        } else return true;
    }

    private boolean isDateValid(List<Calendar> dates, int hour, int minute){
        if(dates.size() == 0){
            Toast.makeText(getApplicationContext(), "Seleziona almeno una data.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(dates.size() > 20){          //IMPOSTA LIMITE DATE SELEZIONABILI
            Toast.makeText(getApplicationContext(), "Seleziona al massimo 20 date.", Toast.LENGTH_LONG).show();
            return false;
        }

        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

         for (Calendar selectedDate : dates){
             int sYear = selectedDate.get(Calendar.YEAR);
             int sMonth = selectedDate.get(Calendar.MONTH);
             int sDay = selectedDate.get(Calendar.DAY_OF_MONTH);
             if( (sYear < currentYear) || (sYear == currentYear && sMonth < currentMonth)
                     || (sYear == currentYear && sMonth == currentMonth && sDay < currentDay)
                     || (sYear == currentYear && sMonth == currentMonth && sDay == currentDay && hour < currentHour)
                     || (sYear == currentYear && sMonth == currentMonth && sDay == currentDay && hour == currentHour && minute < currentMinute)) {
                 Toast.makeText(getApplicationContext(), "Le date selezionate non possono essere nel passato.", Toast.LENGTH_LONG).show();
                 return false;
             }

         }
        return true;
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
                    isPhotoSelected = true;

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

                isPhotoSelected = true;
            }
        }
    }

    private void uploadEsperienzaImage(Uri filePath) {
        Bitmap bitmap = getImageResized(getApplicationContext(), filePath);
        Uri uriCompressed = createImageFileEsperienza(bitmap);

        if (uriCompressed != null) {
            final EditText titoloEditText = findViewById(R.id.titolo_edit);
            String titolo =  titoloEditText.getText().toString();

            final StorageReference ref = storageReference.child("images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().concat("/")+"esperienze/"+titolo);
            ref.putFile(uriCompressed);
        }

    }

    /*
            *Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     */
    private static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d("", "resizer: new bitmap width = " + bm.getWidth());
            i++;
        } while (bm.getWidth() < minWidthQuality && i < sampleSizes.length);
        return bm;
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        Log.d("", options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
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


    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
}

