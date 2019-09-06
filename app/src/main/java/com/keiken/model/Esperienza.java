package com.keiken.model;

import java.util.*;

public class Esperienza {

    private String titolo, descrizione, luogo, ID_CREATORE, prezzo;
    private ArrayList<String> categorie;
    private HashMap<Calendar, Long> date;
    private long ore, minuti, nPostiDisponibili;
    private String photoUri;

    public Esperienza(String titolo, String descrizione, String luogo, String ID_CREATORE, String prezzo, ArrayList<String> categorie, HashMap<Calendar, Long> date, long ore, long minuti, long nPostiDisponibili, String photoUri) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.luogo = luogo;
        this.ID_CREATORE = ID_CREATORE;
        this.prezzo = prezzo;
        this.categorie = new ArrayList<String>(categorie);
        this.date = date;
        this.ore = ore;
        this.minuti = minuti;
        this.nPostiDisponibili = nPostiDisponibili;
        this.photoUri = photoUri;
    }

    // GETTERS & SETTERS
    public String getPhotoUri() { return photoUri; }
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

    public HashMap<Calendar, Long> getDate() {
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


