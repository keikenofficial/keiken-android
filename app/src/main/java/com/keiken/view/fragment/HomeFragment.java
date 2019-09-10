package com.keiken.view.fragment;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonSerializer;
import com.jaygoo.widget.RangeSeekBar;
import com.keiken.R;
import com.keiken.model.Esperienza;
import com.keiken.view.IOnBackPressed;
import com.keiken.view.RVAdapterHome;
import com.keiken.view.RVAdapterProfile;
import com.keiken.view.activity.CreateExperienceActivity;
import com.keiken.view.activity.ViewExperienceActivity;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import java.io.IOException;
import java.sql.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class HomeFragment extends Fragment implements IOnBackPressed {

    private LinearLayout  backgroundFrame;
    private BackdropFrontLayerBehavior sheetBehavior;
    private MaterialButton filterButton;
    private ImageView upArrow;
    private TextView header1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    // activity listener interface
    private OnPageListener pageListener;
    public interface OnPageListener {
        public void onPage1(String s);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoricoGiorno1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        FrameLayout c = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




        header1 = c.findViewById(R.id.header1);

        final BackdropFrontLayer contentLayout = c.findViewById(R.id.backdrop);
        contentLayout.setTouchIntercept(BackdropFrontLayer.NONE);

        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(true);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        final RecyclerView rv = c.findViewById(R.id.esperienze);

        final SwipeRefreshLayout pullToRefresh = c.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                db.collection("esperienze").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            final ArrayList<Esperienza> esperienze = new ArrayList<>();

                            //ArrayList<String> lista = new ArrayList<String>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                if (document.exists()) {


                                    //inizializzazione dati con valori presi dal DB

                                    final Esperienza e;
                                    final String titolo = (String) document.get("titolo");
                                    final String descrizione = (String) document.get("descrizione");
                                    final String luogo = (String) document.get("luogo");
                                    final String ID_CREATORE = (String) document.get("ID_CREATORE");
                                    final String prezzo = (String) document.get("prezzo");
                                    final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document.get("categorie"));
                                    final long ore = (Long) document.get("ore");
                                    final long minuti = (Long) document.get("minuti");
                                    final long nPostiDisponibili = (Long) document.get("posti_massimi");
                                    final String photoUri = (String) document.get("photoUri");
                                    final String ID_ESPERIENZA =(String) document.getId();
                                    //GET CALENDARIO

                                    db.collection("esperienze").document(document.getId()).collection("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Esperienza e;
                                                HashMap<Date, Long> date = new HashMap<Date, Long>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.exists()) {
                                                        //Long tempTimestamp = (Long) ((HashMap<String, Object>) document.get("data")).get("timeInMillis");
                                                        //Calendar tempCalendar = new GregorianCalendar();
                                                        //tempCalendar.setTimeInMillis(tempTimestamp);
                                                        Timestamp timestamp = (Timestamp) document.get("data");
                                                        Date data = timestamp.toDate();
                                                        Long nPostiDisponibili = (Long) document.get("posti_disponibili");
                                                        date.put(data, nPostiDisponibili);
                                                    } else {
                                                        Log.d("", "No such document");
                                                    }
                                                }
                                                e = new Esperienza(titolo, descrizione, luogo, ID_CREATORE, prezzo, categorie, date, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                                if (!e.getID_CREATORE().equals(mAuth.getCurrentUser().getUid()))
                                                    esperienze.add(e);


                                                RVAdapterHome adapter = new RVAdapterHome(esperienze, new RVAdapterHome.OnItemClickListener() {
                                                    @Override

                                                    public void onItemClick(Esperienza esperienza) {
                                                        Intent i = new Intent(getContext(), ViewExperienceActivity.class);
                                                        i.putExtra("titolo", esperienza.getTitolo());
                                                        i.putExtra("descrizione", esperienza.getDescrizione());
                                                        i.putExtra("luogo", esperienza.getLuogo());
                                                        i.putExtra("ID_CREATORE", esperienza.getID_CREATORE());
                                                        i.putExtra("prezzo", esperienza.getPrezzo());
                                                        i.putExtra("categorie", esperienza.getCategorie());
                                                        i.putExtra("ore", Long.toString(esperienza.getOre()));
                                                        i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                        i.putExtra("nPostiDisponibili", Long.toString(esperienza.getnPostiDisponibili()));
                                                        i.putExtra("photoUri", esperienza.getPhotoUri());
                                                        i.putExtra("date", esperienza.getDate());
                                                        i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());

                                                        startActivity(i);
                                                    }
                                                });
                                                rv.setAdapter(adapter);

                                            } else {
                                                Log.d("", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    Log.d("", "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d("", "No such document");
                                }
                            }
                        }
                        else {
                            Log.d("", "get failed with ", task.getException());
                        }
                    }
                });
                pullToRefresh.setRefreshing(false);
            }
        });






        filterButton = c.findViewById(R.id.filter_button);
        upArrow = c.findViewById(R.id.up_arrow);



        contentLayout.setBehavior(sheetBehavior);
        contentLayout.setButton(filterButton);
        contentLayout.setDrawable((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.cross_to_filter));

        contentLayout.setImageView(upArrow);
        contentLayout.setDrawable2((AnimatedVectorDrawable)getResources().getDrawable(R.drawable.black_to_white_up_arrow));





        final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        backgroundFrame = c.findViewById(R.id.background_frame);
        final int bFrameHeight = backgroundFrame.getBottom();

        //sheetBehavior.setPeekHeight(displayMetrics.heightPixels-bFrameHeight);


        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int)( 80 * (displayMetrics.densityDpi / 160f));
                    int bottomPx = (int)( 38 * (displayMetrics.densityDpi / 160f));


                    sheetBehavior.setPeekHeight(bottomPx);

                }
            });
        }











        Toolbar toolbar = c.findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);









        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
         backgroundFrame = c.findViewById(R.id.background_frame);
        //final int bFrameHeight = backgroundFrame.getBottom();
        //sheetBehavior.setPeekHeight(displayMetrics.heightPixels-bFrameHeight);



        upArrow.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

               filterButton.setIcon(getResources().getDrawable(R.drawable.cross_to_filter));
               AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)filterButton.getIcon();
               ic.start();


               upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
               AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
               ic2.start();




           }
       });

        final MaterialButton filtraBT = c.findViewById(R.id.filtra);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                    //  recursiveLoopChildren(false, contentLayout);

                    filterButton.setIcon(getResources().getDrawable(R.drawable.filter_to_cross));
                    AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)filterButton.getIcon();
                    ic.start();

                    upArrow.setImageDrawable((getResources().getDrawable(R.drawable.white_to_black_up_arrow)));
                    AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
                    ic2.start();

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                    filtraBT.setEnabled(true);


                }
                else {

                    filterButton.setIcon(getResources().getDrawable(R.drawable.cross_to_filter));
                    AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)filterButton.getIcon();
                    ic.start();

                    upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
                    AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
                    ic2.start();

                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    // recursiveLoopChildren(true, contentLayout);



                }
            }
        });





        //FILTRI


        final EditText luogoET = c.findViewById(R.id.luogo);

        final RangeSeekBar prezzoRSB = c.findViewById(R.id.seekbar);
        prezzoRSB.setRight(9999);
        prezzoRSB.setLeft(0);
        prezzoRSB.setIndicatorTextDecimalFormat("0000");
        prezzoRSB.setEnableThumbOverlap(false);

        final ChipGroup categorieCG = c.findViewById(R.id.categorie);

        final com.shawnlin.numberpicker.NumberPicker numberPicker = (com.shawnlin.numberpicker.NumberPicker) c.findViewById(R.id.posti_selezionati);
        numberPicker.setDividerColorResource(R.color.White);
        numberPicker.setTextColorResource(R.color.White);
        numberPicker.setSelectedTextColorResource(R.color.Gray);


        final DateRangeCalendarView dateDRCV = c.findViewById(R.id.calendar);


        filtraBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtraBT.setEnabled(false);
                final ArrayList<Esperienza> esperienze = new ArrayList<>();

                db.collection("esperienze").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            //ArrayList<String> lista = new ArrayList<String>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                if (document.exists()) {


                                    //inizializzazione dati con valori presi dal DB

                                    final Esperienza e;
                                    final String titolo = (String) document.get("titolo");
                                    final String descrizione = (String) document.get("descrizione");
                                    final String luogo = (String) document.get("luogo");
                                    final String ID_CREATORE = (String) document.get("ID_CREATORE");
                                    final String prezzo = (String) document.get("prezzo");
                                    final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document.get("categorie"));
                                    final long ore = (Long) document.get("ore");
                                    final long minuti = (Long) document.get("minuti");
                                    final long nPostiDisponibili = (Long) document.get("posti_massimi");
                                    final String photoUri = (String) document.get("photoUri");
                                    final String ID_ESPERIENZA =(String) document.getId();
                                    //GET CALENDARIO

                                    db.collection("esperienze").document(document.getId()).collection("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Esperienza e;
                                                HashMap<Date, Long> date = new HashMap<Date, Long>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.exists()) {
                                                        //Long tempTimestamp = (Long) ((HashMap<String, Object>) document.get("data")).get("timeInMillis");
                                                        //Calendar tempCalendar = new GregorianCalendar();
                                                        //tempCalendar.setTimeInMillis(tempTimestamp);
                                                        Timestamp data = (Timestamp) document.get("data");
                                                        Long nPostiDisponibili = (Long) document.get("posti_disponibili");
                                                        date.put(data.toDate(), nPostiDisponibili);
                                                    } else {
                                                        Log.d("", "No such document");
                                                    }
                                                }
                                                e = new Esperienza(titolo, descrizione, luogo, ID_CREATORE, prezzo, categorie, date, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                                boolean da_aggiungere = true;
                                                if (e.getID_CREATORE().equals(mAuth.getCurrentUser().getUid()))
                                                    da_aggiungere = false;




                                                final String luogo = luogoET.getText().toString();
                                                if(!luogo.equals("")){
                                                        if(!luogo.equals(e.getLuogo())){
                                                            da_aggiungere = false;
                                                        }
                                                }

                                                float left_bar = prezzoRSB.getLeftSeekBar().getProgress();
                                                float right_bar = prezzoRSB.getRightSeekBar().getProgress();
                                                if(!(left_bar == 0) || !(right_bar == 250)) {
                                                    if(!(right_bar == 250) && !(left_bar == 0)) {
                                                        if (Float.parseFloat(e.getPrezzo()) < left_bar || Float.parseFloat(e.getPrezzo()) > right_bar)
                                                            da_aggiungere = false;
                                                    } else if (!(left_bar == 0) && (right_bar == 250)){
                                                        if (Float.parseFloat(e.getPrezzo()) < left_bar) {
                                                            da_aggiungere = false;
                                                        }
                                                    } else if ((left_bar == 0) && !(right_bar == 250)) {
                                                        if (Float.parseFloat(e.getPrezzo()) > right_bar) {
                                                            da_aggiungere = false;
                                                        }
                                                    }
                                                }
                                                ArrayList<String> categorie = new ArrayList<String>();

                                                for (int i = 0; i < categorieCG.getChildCount(); i++){
                                                    Chip tempChip = (Chip) categorieCG.getChildAt(i);
                                                    if(tempChip.isChecked()){
                                                        categorie.add(tempChip.getText().toString());
                                                    }
                                                }
                                                if(categorie.size()>0) {
                                                        boolean containsCat = false;
                                                        for (String c : categorie) {
                                                            if (e.getCategorie().contains(c)) {
                                                                containsCat = true;
                                                                continue;
                                                            }
                                                        }
                                                        if (!containsCat)
                                                            da_aggiungere = false;
                                                }
                                                int posti_selezionati = numberPicker.getValue();

                                                    if(e.getnPostiDisponibili() < posti_selezionati)
                                                       da_aggiungere = false;



                                                Calendar startCal = dateDRCV.getStartDate();
                                                Calendar endCal = dateDRCV.getEndDate();
                                                if(startCal != null && endCal != null) {
                                                    Date start_day = startCal.getTime();
                                                    Date end_day = endCal.getTime();
                                                    if (!(start_day == null) && !(end_day == null)) {
                                                            HashMap<Date, Long> dateMap = new HashMap<Date, Long>(e.getDate());
                                                            ArrayList<Date> dateEsperienza = (ArrayList<Date>) dateMap.keySet();
                                                            Collections.sort(dateEsperienza);
                                                            if ((dateEsperienza.get(0).compareTo(end_day) > 0) || (dateEsperienza.get(dateEsperienza.size()).compareTo(start_day) < 0)) {
                                                                da_aggiungere = false;
                                                            }
                                                    }
                                                }

                                                if(da_aggiungere)
                                                    esperienze.add(e);

                                                RVAdapterHome adapter = new RVAdapterHome(esperienze, new RVAdapterHome.OnItemClickListener() {
                                                    @Override

                                                    public void onItemClick(Esperienza esperienza) {
                                                        Intent i = new Intent(getContext(), ViewExperienceActivity.class);
                                                        i.putExtra("titolo", esperienza.getTitolo());
                                                        i.putExtra("descrizione", esperienza.getDescrizione());
                                                        i.putExtra("luogo", esperienza.getLuogo());
                                                        i.putExtra("ID_CREATORE", esperienza.getID_CREATORE());
                                                        i.putExtra("prezzo", esperienza.getPrezzo());
                                                        i.putExtra("categorie", esperienza.getCategorie());
                                                        i.putExtra("ore", Long.toString(esperienza.getOre()));
                                                        i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                        i.putExtra("nPostiDisponibili", Long.toString(esperienza.getnPostiDisponibili()));
                                                        i.putExtra("photoUri", esperienza.getPhotoUri());
                                                        i.putExtra("date", esperienza.getDate());
                                                        i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());

                                                        startActivity(i);
                                                    }
                                                });
                                                rv.setAdapter(adapter);


                                            } else {
                                                Log.d("", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    Log.d("", "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d("", "No such document");
                                }
                            }
                        }
                        else {
                            Log.d("", "get failed with ", task.getException());
                        }
                    }
                });





                /* GET FROM DB
                try{
                    db.collection("esperienze").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {

                                final ArrayList<Esperienza> esperienze = new ArrayList<>();

                                //ArrayList<String> lista = new ArrayList<String>();

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                }
                            }
                        }
                    });

                }catch (NullPointerException e){

                }*/




                filterButton.setIcon(getResources().getDrawable(R.drawable.cross_to_filter));
                AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)filterButton.getIcon();
                ic.start();

                upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
                AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
                ic2.start();

                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });





//////////////////////////////////////////////////////////////////////////////////////////////////////



        ///////////////////////////// VISUALIZZA ELENCO PROPRIE ESPERIENZE ///////////////////////////////
        /*
        result = new ArrayList<Esperienza>();
        downloadExperiencesByUID();  // NON ENTRA NEL: "ON COMPLETE"
        ArrayList<Esperienza> esperienze = new ArrayList<Esperienza>(result);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        RecyclerView rv = c.findViewById(R.id.esperienze);
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(esperienze, new RVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Esperienza esperienza) {
                startActivity(new Intent(getContext(), CreateExperienceActivity.class));
            }
        });
        rv.setAdapter(adapter);
        rv.setFocusable(false);
        rv.setHasFixedSize(true);

        RecyclerViewHeader headerRV = c.findViewById(R.id.rvHeader);
        headerRV.attachTo(rv);*/

        rv.setLayoutManager(llm);

        rv.setFocusable(false);
        rv.setHasFixedSize(true);



        //QUERY DAL DATABASE PER RICEVERE LE VARIE ESPERIENZE

        db.collection("esperienze").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    final ArrayList<Esperienza> esperienze = new ArrayList<>();

                    //ArrayList<String> lista = new ArrayList<String>();

                    for (QueryDocumentSnapshot document : task.getResult()) {


                        if (document.exists()) {


                            //inizializzazione dati con valori presi dal DB

                            final Esperienza e;
                            final String titolo = (String) document.get("titolo");
                            final String descrizione = (String) document.get("descrizione");
                            final String luogo = (String) document.get("luogo");
                            final String ID_CREATORE = (String) document.get("ID_CREATORE");
                            final String prezzo = (String) document.get("prezzo");
                            final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document.get("categorie"));
                            final long ore = (Long) document.get("ore");
                            final long minuti = (Long) document.get("minuti");
                            final long nPostiDisponibili = (Long) document.get("posti_massimi");
                            final String photoUri = (String) document.get("photoUri");
                            final String ID_ESPERIENZA =(String) document.getId();
                            //GET CALENDARIO

                            db.collection("esperienze").document(document.getId()).collection("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Esperienza e;
                                        HashMap<Date, Long> date = new HashMap<Date, Long>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.exists()) {
                                                //Long tempTimestamp = (Long) ((HashMap<String, Object>) document.get("data")).get("timeInMillis");
                                                //Calendar tempCalendar = new GregorianCalendar();
                                                //tempCalendar.setTimeInMillis(tempTimestamp);
                                                Timestamp data = (Timestamp) document.get("data");
                                                Long nPostiDisponibili = (Long) document.get("posti_disponibili");
                                                date.put(data.toDate(), nPostiDisponibili);


                                            } else {
                                                Log.d("", "No such document");
                                            }
                                        }
                                        e = new Esperienza(titolo, descrizione, luogo, ID_CREATORE, prezzo, categorie, date, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                        if (!e.getID_CREATORE().equals(mAuth.getCurrentUser().getUid()))
                                            esperienze.add(e);


                                        RVAdapterHome adapter = new RVAdapterHome(esperienze, new RVAdapterHome.OnItemClickListener() {
                                            @Override

                                            public void onItemClick(Esperienza esperienza) {
                                                Intent i = new Intent(getContext(), ViewExperienceActivity.class);
                                                i.putExtra("titolo", esperienza.getTitolo());
                                                i.putExtra("descrizione", esperienza.getDescrizione());
                                                i.putExtra("luogo", esperienza.getLuogo());
                                                i.putExtra("ID_CREATORE", esperienza.getID_CREATORE());
                                                i.putExtra("prezzo", esperienza.getPrezzo());
                                                i.putExtra("categorie", esperienza.getCategorie());
                                                i.putExtra("ore", Long.toString(esperienza.getOre()));
                                                i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                i.putExtra("nPostiDisponibili", Long.toString(esperienza.getnPostiDisponibili()));
                                                i.putExtra("photoUri", esperienza.getPhotoUri());
                                                i.putExtra("date", esperienza.getDate());
                                                i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());

                                                startActivity(i);
                                            }
                                        });
                                        rv.setAdapter(adapter);

                                    } else {
                                        Log.d("", "get failed with ", task.getException());
                                    }
                                }
                            });
                            Log.d("", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("", "No such document");
                        }
                    }
                }
                    else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });







        return c;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    // onAttach : set activity listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // if implemented by activity, set listener
        if(activity instanceof OnPageListener) {
            pageListener = (OnPageListener) activity;
        }
        // else create local listener (code never executed in this example)
        else pageListener = new OnPageListener() {
            @Override
            public void onPage1(String s) {
                Log.d("PAG1","Button event from page 1 : "+s);
            }
        };
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




  /*  public void recursiveLoopChildren(boolean enable, ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);

            child.setEnabled(enable);

            if (child instanceof ViewGroup) {
                recursiveLoopChildren(enable, (ViewGroup) child);
            }
        }
    }*/

    @Override
    public boolean onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            //action not popBackStack

            filterButton.setIcon(getResources().getDrawable(R.drawable.cross_to_filter));
            AnimatedVectorDrawable ic =  (AnimatedVectorDrawable)filterButton.getIcon();
            ic.start();

            upArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
            AnimatedVectorDrawable ic2 =  (AnimatedVectorDrawable)upArrow.getDrawable();
            ic2.start();

            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);






            return true;
        } else return true;
    }


    }

