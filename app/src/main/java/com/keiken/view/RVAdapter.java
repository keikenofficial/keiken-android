package com.keiken.view;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keiken.Esperienza.Esperienza;
import com.keiken.R;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class RVAdapter extends  RecyclerView.Adapter<RVAdapter.ExperienceViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Esperienza esperienza);
    }

    private List<Esperienza> esperienze;
    private final OnItemClickListener listener;

    public RVAdapter(List<Esperienza> esperienze, OnItemClickListener listener) {
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

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nome;
        ImageView foto;

        public ExperienceViewHolder(final View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            nome = (TextView)itemView.findViewById(R.id.nome);
            foto = (ImageView)itemView.findViewById(R.id.foto);
        }

        public void bind(final Esperienza ristorante, final OnItemClickListener listener) {
            nome.setText(ristorante.nome);
            foto.setImageResource(ristorante.fotoId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(ristorante);
                }
            });
        }



    }


}