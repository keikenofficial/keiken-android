package com.keiken.view;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.keiken.R;
import com.keiken.controller.ImageController;
import com.keiken.model.Esperienza;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapterProfile extends  RecyclerView.Adapter<RVAdapterProfile.ExperienceViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Esperienza esperienza);
    }

    private List<Esperienza> esperienze;
    private final OnItemClickListener listener;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    public RVAdapterProfile(List<Esperienza> esperienze, OnItemClickListener listener) {
        this.esperienze = esperienze;
        this.listener = listener;
    }

    @Override
    public ExperienceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience, parent, false);
        ExperienceViewHolder vh = new ExperienceViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ExperienceViewHolder holder, int i) {
        holder.bind(esperienze.get(i), listener);
    }


    @Override
    public int getItemCount() {
        return esperienze.size();
    }

    public class ExperienceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView titolo;
        TextView luogo;
        RatingBar recensioni;
        TextView user_name;
        ImageView profile_pic;
        TextView categorie;
        TextView prezzo;
        MaterialCardView profile_pic_ontainer;
        ImageView foto;

        public ExperienceViewHolder(final View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            titolo = itemView.findViewById(R.id.titolo);
            luogo = itemView.findViewById(R.id.luogo);
            recensioni = itemView.findViewById(R.id.rating);
            user_name = itemView.findViewById(R.id.user_name);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            categorie = itemView.findViewById(R.id.categorie);
            prezzo = itemView.findViewById(R.id.prezzo);
            profile_pic_ontainer = itemView.findViewById(R.id.profile_pic_ontainer);
            foto = itemView.findViewById(R.id.foto);
        }

        public void bind(final Esperienza e, final OnItemClickListener listener) {
            titolo.setText(e.getTitolo());
            luogo.setText(e.getLuogo());
            prezzo.setText(e.getPrezzo()+"\u20ac");
            //recensioni
            //
            ////////////

            //DOWNLOAD IMMAGINE ESPERIENZA
            try {
                mAuth = FirebaseAuth.getInstance();

                db = FirebaseFirestore.getInstance();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();

                storageReference.child(e.getPhotoUri())
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
            } catch (Exception ex) {};







            user_name.setVisibility(View.GONE);  //NEL PROPRIO PROFILO NOME UTENTE E
            profile_pic.setVisibility(View.GONE);     // FOTO PROFILO NON VENGONO MOSTRATI
            profile_pic_ontainer.setVisibility(View.GONE);
            String categorieString = "";
            for(String temp : e.getCategorie()){
                categorieString = categorieString.concat("#" + temp + " ");
            }
            categorie.setText(categorieString);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(e);
                }
            });
        }

    }

}