package com.keiken.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.model.Esperienza;
import com.keiken.model.Prenotazione;
import com.keiken.view.RVAdapterExperience;
import com.keiken.view.RVAdapterExperienceRicevuta;
import com.keiken.view.RVAdapterHome;
import com.keiken.view.RVAdapterProfile;
import com.keiken.view.activity.ViewBookingActivity;
import com.keiken.view.activity.ViewExperienceActivity;
import com.keiken.view.backdrop.BackdropFrontLayer;
import com.keiken.view.backdrop.BackdropFrontLayerBehavior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExperiencesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExperiencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExperiencesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout  backgroundFrame;
    private BackdropFrontLayerBehavior sheetBehavior, sheetBehaviorReviews;
    private RecyclerView rv, rv2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private ImageView downArrow;
    private LinearLayoutCompat header;
    private FrameLayout c;


    // activity listener interface
    private OnPageListener pageListener;
    public interface OnPageListener {
        public void onPage1(String s);
    }

    public ExperiencesFragment() {
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
    public static MessagingFragment newInstance(String param1, String param2) {
        MessagingFragment fragment = new MessagingFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        c = (FrameLayout) inflater.inflate(R.layout.fragment_experiences, container, false);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        LinearLayout contentLayout = c.findViewById(R.id.backdrop);

        downArrow = c.findViewById(R.id.down_arrow);
        header = c.findViewById(R.id.header);



///////////////////////////////////////////////// PRENOTAZIONI EFFETTUATE//////////
        sheetBehavior = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayout);
        sheetBehavior.setFitToContents(false);
        sheetBehavior.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);//initially state to fully expanded

        final BackdropFrontLayer contentLayoutReviews = c.findViewById(R.id.backdrop_reviews);
        sheetBehaviorReviews = (BackdropFrontLayerBehavior) BottomSheetBehavior.from(contentLayoutReviews);
        sheetBehaviorReviews.setFitToContents(false);
        //sheetBehaviorReviews.setHideable(false);//prevents the boottom sheet from completely hiding off the screen
        sheetBehaviorReviews.setState(BottomSheetBehavior.STATE_COLLAPSED);//initially state to fully expanded
        contentLayoutReviews.setBehavior(sheetBehaviorReviews);


        backgroundFrame = c.findViewById(R.id.background_frame_x);
        final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int)( 80 * (displayMetrics.densityDpi / 160f));
                    int bottomBarPx = (int)( 56 * (displayMetrics.densityDpi / 160f));

                    int peekHeight = displayMetrics.heightPixels-viewHeight-toolbarPx-bottomBarPx;


                    sheetBehavior.setPeekHeight(peekHeight);
                    //int bottomPx = (int)( 70 * (displayMetrics.densityDpi / 160f));
                    //sheetBehaviorReviews.setPeekHeight(bottomPx);

                    int marginPx = (int)( 20 * (displayMetrics.densityDpi / 160f));
                    sheetBehaviorReviews.setPeekHeight(160);

                }
            });
        }

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehaviorReviews.setState(BottomSheetBehavior.STATE_COLLAPSED);
                downArrow.setImageDrawable((getResources().getDrawable(R.drawable.black_to_white_up_arrow)));
                AnimatedVectorDrawable ic2 = (AnimatedVectorDrawable) downArrow.getDrawable();
                ic2.start();
            }
        });

        View.OnClickListener listener = (new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sheetBehaviorReviews.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    //  recursiveLoopChildren(false, contentLayout);

                    downArrow.setImageDrawable((getResources().getDrawable(R.drawable.white_to_black_up_arrow)));
                    AnimatedVectorDrawable ic2 = (AnimatedVectorDrawable) downArrow.getDrawable();
                    ic2.start();

                    sheetBehaviorReviews.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                else {

                    downArrow.setImageDrawable((getResources().getDrawable(R.drawable.ic_down_arrow)));
                    
                    sheetBehaviorReviews.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        });

        header.setOnClickListener(listener);
        downArrow.setOnClickListener(listener);

////////////////////////////////////////


        /*final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        backgroundFrame = c.findViewById(R.id.background_frame_x);



        ViewTreeObserver viewTreeObserver = backgroundFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    backgroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewHeight = backgroundFrame.getBottom();

                    int toolbarPx = (int) (80 * (displayMetrics.densityDpi / 160f));
                    int bottomBarPx = (int) (56 * (displayMetrics.densityDpi / 160f));

                    int peekHeight = displayMetrics.heightPixels - viewHeight - toolbarPx - bottomBarPx;


                    sheetBehavior.setPeekHeight(peekHeight);
                    //int bottomPx = (int)( 70 * (displayMetrics.densityDpi / 160f));
                    //sheetBehaviorReviews.setPeekHeight(bottomPx);

                    int marginPx = (int) (20 * (displayMetrics.densityDpi / 160f));

                }
            });
        }*/




        //TOOLBAR//////////////////////////////////////////////////////////
        Toolbar toolbar = c.findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Esperienze");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        /////////////////////////////////////////////////////////////////////


        //POPOLO LA RECYCLER VIEW
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        final LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        final LinearLayoutManager llm3 = new LinearLayoutManager(getActivity());
        final LinearLayoutManager llm4 = new LinearLayoutManager(getActivity());




        final SwipeRefreshLayout pullToRefreshMade = c.findViewById(R.id.swiperefresh_made);
        pullToRefreshMade.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showBookingEffettuata(c, llm3);
                pullToRefreshMade.setRefreshing(false);
            }
        });


        final SwipeRefreshLayout pullToRefreshReceived = c.findViewById(R.id.swiperefresh_received);
        pullToRefreshReceived.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showBookingRicevuta(c, llm4);
                pullToRefreshReceived.setRefreshing(false);
            }
        });

        showBookingEffettuata(c, llm);
        showBookingRicevuta(c, llm2);


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


    @Override
    public void onResume() {
        super.onResume();

        final LinearLayoutManager llm5 = new LinearLayoutManager(getActivity());
        final LinearLayoutManager llm6 = new LinearLayoutManager(getActivity());
        showBookingRicevuta(c,llm5);
        showBookingEffettuata(c, llm6);


    }

    //EFFETTUATE
    public void showBookingEffettuata(FrameLayout c, LinearLayoutManager llm){
        //QUERY DAL DATABASE PER RICEVERE LE VARIE ESPERIENZE PRENOTATE    (PRENOTAZIONI EFFETTUATE)
        rv = c.findViewById(R.id.esperienze_in_corso);
        rv.setLayoutManager(llm);

        rv.setFocusable(false);
        rv.setHasFixedSize(true);

        final CollectionReference prenotazioni = db.collection("prenotazioni");
        Query query = prenotazioni.whereEqualTo("ID_PRENOTANTE", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Task<QuerySnapshot> querySnapshotTask = query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //ARRAY DA PASSARE AL RVAdapterExperience
                    final ArrayList<Esperienza> esperienze = new ArrayList<Esperienza>();


                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {

                            //recupero ID_ESPERIENZA ed ID_CREATORE_ESPERIENZA per ogni prenotazione
                            //da cui poi ricavare l'esperienza e la foto profilo/nome profilo da stampare
                            final String ID_ESPERIENZA_PRENOTATA = (String) document.get("ID_ESPERIENZA");
                            final String ID_CREATORE_ESPERIENZA = (String) document.get("ID_CREATORE_ESPERIENZA");
                            final String ID_PRENOTANTE = (String) document.get("ID_PRENOTANTE");
                            final String ID_PRENOTAZIONE = document.getId();
                            final long posti_prenotati = ((Long)document.get("posti_prenotati"));
                            final String ore = (String) document.get("ore");
                            final String minuti = (String) document.get("minuti");
                            final String prezzo = (String) document.get("prezzo");
                            final boolean isAccepted = (boolean) document.get("isAccepted");

                            //DA AGGIUNGERE CONTROLLO SE LA VARIABILE IS ACCEPTED è TRUE  O FALSE
                            // IN MODO DA MOSTRARE NEI DUE CASI UNA ICONA DI CONFERMA O MENO DELLA PRENOTAZIONE

                            //RECUPERO LA DATA PASSANDO PER IL TIMESTAMP
                            Date tempDate = new Date(System.currentTimeMillis());
                            Timestamp timestamp = (Timestamp) document.get("data_selezionata");
                            final Date data_prenotazione = timestamp.toDate();

                            //Controllo che la data della prenotazione sia effettivamente futura
                            if(Long.compare(data_prenotazione.getTime(), tempDate.getTime()) >= 0) {
                                //Raccolgo nome utente e foto profilo
                                final DocumentReference utenti = db.collection("utenti").document(ID_CREATORE_ESPERIENZA); //ANDREBBE PRESO SOLO IL DOCUMENTO , NON AVENDO L'ID DEL DOCUMENTO MA  DELL'UTENTE BISOGNA ITERARE IL CONTROLLO ANCHE SE DARà SOLO UN RISULTATO SEMPRE -> 1 SOLO ID PER UTENTE
                                //TO-DO : SALVARE E PASSARE L'ID DOCUMENTO DEL CREATORE DELL'ESPERIENZA PER ALLEGGERIRE LA QUERY
                                utenti.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document2 = task.getResult();
                                                if(document2.exists()) {
                                                    final String nome_utente_creatore = (String) document2.get("name"); // creatore
                                                    final String photo_url_creatore_esperienza = (String) document2.get("photoUrl");

                                                    //Raccolgo informazioni esperienza (immagine, etc)
                                                    final DocumentReference esperienza = db.collection("esperienze").document(ID_ESPERIENZA_PRENOTATA);
                                                    esperienza.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document3 = task.getResult();
                                                                if (document3.exists()) {

                                                                    final Esperienza e;
                                                                    final String titolo = (String) document3.get("titolo");
                                                                    final String descrizione = (String) document3.get("descrizione");
                                                                    final String luogo = (String) document3.get("luogo");
                                                                    final String ID_CREATORE = (String) document3.get("ID_CREATORE");
                                                                    final String prezzo = (String) document3.get("prezzo");
                                                                    final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document3.get("categorie"));
                                                                    final Long ore = (Long) document3.get("ore");
                                                                    final long minuti = (Long) document3.get("minuti");
                                                                    final long nPostiDisponibili = (Long) document3.get("posti_massimi");
                                                                    final String photoUri = (String) document3.get("photoUri");
                                                                    String ID_ESPERIENZA =(String) document3.getId();

                                                                    e = new Esperienza(titolo, descrizione, luogo, ID_CREATORE, prezzo, categorie, data_prenotazione, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                                                    esperienze.add(e);


                                                                    //ARRIVATO A QUESTO PUNTO, PROCESSO TUTTI I DATI
                                                                    //STAMPA SU ADAPTER E INVIO DATI UTILI TRAMITE INTENT
                                                                    RVAdapterExperience adapter = new RVAdapterExperience(esperienze, new RVAdapterExperience.OnItemClickListener() {
                                                                        @Override
                                                                        public void onItemClick(Esperienza esperienza) {
                                                                            Intent i = new Intent(getContext(), ViewBookingActivity.class);
                                                                            //DATI DA PASSARE AL ViewBookingActivity !!!!!!!!!!!!
                                                                            //PARAMETRI ESPERIENZA
                                                                            i.putExtra("titolo", esperienza.getTitolo());
                                                                            i.putExtra("luogo", esperienza.getLuogo());
                                                                            i.putExtra("ID_CREATORE", esperienza.getID_CREATORE());
                                                                            i.putExtra("ore", Long.toString(esperienza.getOre()));   //Prendo ore e minuti dall'esperienza presa dal database perchè potrebbero essere stati aggiornati o modficati se in un futuro permetteremo la modifica di alcunidati di una esperienza
                                                                            i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                                            i.putExtra("photoUri", esperienza.getPhotoUri());
                                                                            i.putExtra("descrizione", esperienza.getDescrizione());
                                                                            i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());
                                                                            i.putExtra("ID_PRENOTANTE", ID_PRENOTANTE);
                                                                            //PARAMETRI CREATORE ESPERIENZA
                                                                            i.putExtra("nome_utente", nome_utente_creatore);
                                                                            i.putExtra("photo_url_creatore_esperienza", photo_url_creatore_esperienza);
                                                                            i.putExtra("photo_url_prenotante_esperienza", "images/"+ID_PRENOTANTE+"/foto_profilo");
                                                                            //PARAMETRI PRENOTAZIONE
                                                                            i.putExtra("posti_prenotati", Long.toString(posti_prenotati));
                                                                            i.putExtra("prezzo", prezzo);
                                                                            i.putExtra("isAccepted", isAccepted);
                                                                            i.putExtra("ID_PRENOTAZIONE", ID_PRENOTAZIONE);
                                                                            //la data viene caricata come stringa, serve solo per essere mostrata all'utente
                                                                            //      String tempDate = "";
                                                                            //      if (data_prenotazione.get(Calendar.DAY_OF_MONTH) < 10)
                                                                            //          tempDate += "0";
                                                                            //      tempDate += data_prenotazione.get(Calendar.DAY_OF_MONTH) + "/";
                                                                            //      if (data_prenotazione.get(Calendar.MONTH) < 10)
                                                                            //          tempDate += "0";
                                                                            //      tempDate += data_prenotazione.get(Calendar.MONTH) + "/" + data_prenotazione.get(Calendar.YEAR);
                                                                            SimpleDateFormat formatYear = new SimpleDateFormat("YYYY");
                                                                            String currentYear = formatYear.format(data_prenotazione.getTime());
                                                                            i.putExtra("data_prenotazione", data_prenotazione.toString().substring(0,10)+" " +currentYear);

                                                                            startActivity(i);
                                                                        }
                                                                    });
                                                                    rv.setAdapter(adapter);
                                                                } else {
                                                                    Log.d("ERROR_DOCUMENT", "No such document");
                                                                }
                                                            } else {
                                                                Log.d("ERROR_TASK", "No such document");
                                                            }
                                                        }
                                                    }); //FINE QUERY ESPERIENZA
                                                } else {
                                                    Log.d("ERROR_DOCUMENT", "No such document");
                                                }

                                        } else {
                                            Log.d("ERROR_TASK", "get failed with ", task.getException());
                                        }
                                    }
                                });
                                //FINE QUERY UTENTE_CREATORE
                            }


                        } else {
                            Log.d("ERROR_DOCUMENT", "No such document");
                        }
                    }
                } else {
                    Log.d("ERROR_TASK", "get failed with ", task.getException());
                }
            }
        });
        //FINE QUERY PRENOTAZIONI
    }

    //RICEVUTE
    public void showBookingRicevuta(FrameLayout c, LinearLayoutManager llm2){
        //POPOLO LA RECYCLER VIEW
        rv2 = c.findViewById(R.id.esperienze_ricevute);
        rv2.setLayoutManager(llm2);

        rv2.setFocusable(false);
        rv2.setHasFixedSize(true);


        //QUERY DAL DATABASE PER RICEVERE LE PRENOTAZIONI RICEVUTE
        final CollectionReference prenotazioniRicevute = db.collection("prenotazioni");
        Query query2 = prenotazioniRicevute.whereEqualTo("ID_CREATORE_ESPERIENZA", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Task<QuerySnapshot> querySnapshotTask2 = query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //ARRAY DA PASSARE AL RVAdapterExperience
                    final ArrayList<Esperienza> esperienze = new ArrayList<Esperienza>();


                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {

                            //recupero ID_ESPERIENZA ed ID_CREATORE_ESPERIENZA per ogni prenotazione
                            //da cui poi ricavare l'esperienza e la foto profilo/nome profilo da stampare
                            final String ID_CREATORE_ESPERIENZA = (String) document.get("ID_CREATORE_ESPERIENZA");
                            final String ID_ESPERIENZA_PRENOTATA = (String) document.get("ID_ESPERIENZA");
                            final String ID_PRENOTANTE = (String) document.get("ID_PRENOTANTE");
                            final String ID_PRENOTAZIONE = document.getId();

                            final long posti_prenotati = ((Long)document.get("posti_prenotati"));

                            final String ore = (String) document.get("ore");
                            final String minuti = (String) document.get("minuti");

                            final String prezzo = (String) document.get("prezzo");

                            final boolean isAccepted = (boolean) document.get("isAccepted");

                            //DA AGGIUNGERE CONTROLLO SE LA VARIABILE IS ACCEPTED è TRUE  O FALSE
                            // IN MODO DA MOSTRARE NEI DUE CASI UNA ICONA DI CONFERMA O MENO DELLA PRENOTAZIONE


                            //RECUPERO LA DATA PASSANDO PER IL TIMESTAMP
                            Date tempDate = new Date(System.currentTimeMillis());

                            Timestamp timestamp = (Timestamp) document.get("data_selezionata");
                            final Date data_prenotazione = timestamp.toDate();


                            //Controllo che la data della prenotazione sia effettivamente futura
                            if(Long.compare(data_prenotazione.getTime(), tempDate.getTime()) >= 0){

                                //Raccolgo nome utente e foto profilo
                                final DocumentReference utenti = db.collection("utenti").document(ID_PRENOTANTE);
                                utenti.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document2 = task.getResult();
                                                if(document2.exists()) {
                                                    final String nome_utente = (String) document2.get("name"); //prenotante
                                                    final String photo_url_prenotante_esperienza = (String) document2.get("photoUrl");

                                                    //Raccolgo informazioni esperienza (immagine, etc)
                                                    final DocumentReference esperienza = db.collection("esperienze").document(ID_ESPERIENZA_PRENOTATA);
                                                    esperienza.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()) {

                                                                    final Esperienza e;
                                                                    final String titolo = (String) document.get("titolo");
                                                                    final String descrizione = (String) document.get("descrizione");
                                                                    final String luogo = (String) document.get("luogo");
                                                                    final String ID_CREATORE = (String) document.get("ID_CREATORE");
                                                                    final String prezzo = (String) document.get("prezzo");
                                                                    final ArrayList<String> categorie = new ArrayList<String>((ArrayList<String>) document.get("categorie"));
                                                                    final Long ore = (Long) document.get("ore");
                                                                    final long minuti = (Long) document.get("minuti");
                                                                    final long nPostiDisponibili = (Long) document.get("posti_massimi");
                                                                    final String photoUri = (String) document.get("photoUri");
                                                                    String ID_ESPERIENZA =(String) document.getId();

                                                                    e = new Esperienza(titolo, descrizione, luogo, ID_PRENOTANTE, prezzo, categorie, data_prenotazione, ore, minuti, nPostiDisponibili, photoUri, ID_ESPERIENZA);
                                                                    esperienze.add(e);


                                                                    //ARRIVATO A QUESTO PUNTO, PROCESSO TUTTI I DATI
                                                                    //STAMPA SU ADAPTER E INVIO DATI UTILI TRAMITE INTENT
                                                                    RVAdapterExperienceRicevuta adapter = new RVAdapterExperienceRicevuta(esperienze, new RVAdapterExperienceRicevuta.OnItemClickListener() {                                                                        @Override
                                                                    public void onItemClick(Esperienza esperienza) {
                                                                        Intent i = new Intent(getContext(), ViewBookingActivity.class);
                                                                        //DATI DA PASSARE AL ViewBookingActivity !!!!!!!!!!!!
                                                                        //PARAMETRI ESPERIENZA
                                                                        i.putExtra("titolo", esperienza.getTitolo());
                                                                        i.putExtra("luogo", esperienza.getLuogo());
                                                                        i.putExtra("ID_CREATORE", ID_CREATORE);
                                                                        i.putExtra("descrizione", esperienza.getDescrizione());
                                                                        i.putExtra("ID_PRENOTANTE", ID_PRENOTANTE);
                                                                        i.putExtra("ore", Long.toString(esperienza.getOre()));   //Prendo ore e minuti dall'esperienza presa dal database perchè potrebbero essere stati aggiornati o modficati se in un futuro permetteremo la modifica di alcunidati di una esperienza
                                                                        i.putExtra("minuti", Long.toString(esperienza.getMinuti()));
                                                                        i.putExtra("photoUri", esperienza.getPhotoUri());
                                                                        i.putExtra("ID_ESPERIENZA", esperienza.getID_ESPERIENZA());
                                                                        //PARAMETRI CREATORE ESPERIENZA
                                                                        i.putExtra("nome_utente", nome_utente); //prenotante
                                                                        i.putExtra("photo_url_prenotante_esperienza", photo_url_prenotante_esperienza); //prenotante
                                                                        i.putExtra("photo_url_creatore_esperienza", "images/"+esperienza.getID_CREATORE()+"/foto_profilo");
                                                                        //PARAMETRI PRENOTAZIONE
                                                                        i.putExtra("posti_prenotati", Long.toString(posti_prenotati));
                                                                        i.putExtra("prezzo", prezzo);
                                                                        i.putExtra("isAccepted", isAccepted);
                                                                        i.putExtra("ID_PRENOTAZIONE", ID_PRENOTAZIONE);
                                                                        //la data viene caricata come stringa, serve solo per essere mostrata all'utente
                                                                        SimpleDateFormat formatYear = new SimpleDateFormat("YYYY");
                                                                        String currentYear = formatYear.format(data_prenotazione.getTime());
                                                                        i.putExtra("data_prenotazione", data_prenotazione.toString().substring(0,10)+" " +currentYear);

                                                                        startActivity(i);
                                                                    }
                                                                    });
                                                                    rv2.setAdapter(adapter);
                                                                } else {
                                                                    Log.d("ERROR_DOCUMENT", "No such document");
                                                                }
                                                            } else {
                                                                Log.d("ERROR_TASK", "No such document");
                                                            }
                                                        }
                                                    }); //FINE QUERY ESPERIENZA
                                                } else {
                                                    Log.d("ERROR_DOCUMENT", "No such document");
                                                }

                                        } else {
                                            Log.d("ERROR_TASK", "get failed with ", task.getException());
                                        }
                                    }
                                });
                                //FINE QUERY UTENTE_CREATORE
                            }


                        } else {
                            Log.d("ERROR_DOCUMENT", "No such document");
                        }
                    }
                } else {
                    Log.d("ERROR_TASK", "get failed with ", task.getException());
                }
            }
        });
        //FINE QUERY PRENOTAZIONI
    }


}
