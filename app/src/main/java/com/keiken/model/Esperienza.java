package com.keiken.model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;

public class Esperienza {

    private String titolo, descrizione, luogo, ID_CREATORE, prezzo;
    private ArrayList<String> categorie;
    private List<Calendar> date;
    private long ore, minuti, nPostiDisponibili;
    private String photo_uri;

    public Esperienza(String titolo, String descrizione, String luogo, String ID_CREATORE, String prezzo, ArrayList<String> categorie, ArrayList<Calendar> date, long ore, long minuti, long nPostiDisponibili, String photo_uri) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.luogo = luogo;
        this.ID_CREATORE = ID_CREATORE;
        this.prezzo = prezzo;
        this.categorie = new ArrayList<String>(categorie);
        this.date = new ArrayList<Calendar>(date);
        this.ore = ore;
        this.minuti = minuti;
        this.nPostiDisponibili = nPostiDisponibili;
        this.photo_uri = photo_uri;
    }

    // GETTERS & SETTERS
    public String getPhoto_uri() { return photo_uri; }
    public String getID_CREATORE() { return  ID_CREATORE; }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getLuogo() {
        return luogo;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public ArrayList<String> getCategorie() {
        return categorie;
    }

    public List<Calendar> getDate() {
        return date;
    }

    public long getOre() {
        return ore;
    }

    public long getMinuti() {
        return minuti;
    }

    public long getnPostiDisponibili() {
        return nPostiDisponibili;
    }
}


